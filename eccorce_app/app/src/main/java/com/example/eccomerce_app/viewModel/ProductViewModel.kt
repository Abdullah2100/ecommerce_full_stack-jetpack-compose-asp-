package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.dto.ProductDto
import com.example.eccomerce_app.model.DtoToModel.toProduct
import com.example.e_commercompose.model.ProductModel
import com.example.eccomerce_app.model.ProductVariantSelection
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.Room.Dao.CurrencyDao
import com.example.eccomerce_app.data.repository.ProductRepository
import com.example.eccomerce_app.util.General.convertPriceToAnotherCurrency
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID


class ProductViewModel(
    private val productRepository: ProductRepository,
    private val currencyDao: CurrencyDao,
    private val scop: CoroutineScope,
//    val webSocket: HubConnection?

) : ViewModel() {


//     val _hub = MutableStateFlow<HubConnection?>(null)

    private val _products = MutableStateFlow<List<ProductModel>?>(null)
    val products = _products.asStateFlow()


    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }

    fun setDefaultCurrency(symbol: String, onCompleteUpdateValue: (value: Boolean) -> Unit) {
        scop.launch(Dispatchers.IO + SupervisorJob()) {
            delay(100)
            currencyDao.setSelectedCurrency(symbol)
            async { convertProductCurrencyToSavedCurrency() }.await()
            onCompleteUpdateValue.invoke(false);
        }
    }

    //this if user change the currency from setting
    private suspend fun convertProductCurrencyToSavedCurrency(): Boolean {
        val currencyList = currencyDao.getSavedCurrencies()
        if (!_products.value.isNullOrEmpty() && currencyList.isNotEmpty()) {
            val targetCurrency = currencyList.firstOrNull { it -> it.isSelected }

            val productToNewCurrency = _products.value?.map { data ->
                data.copy(
                    price = convertPriceToAnotherCurrency(
                        data.price,
                        data.symbol,
                        targetCurrency,
                        currencyList
                    ),
                    symbol = targetCurrency?.symbol ?: data.symbol
                )
            }

            _products.emit(null)
            _products.emit(productToNewCurrency)
            return true

        }
        return false
    }


    //this if the user is select the currency then for api  comming will use this
    // to convert them to local currency saved
    private suspend fun convertProductCurrencyToSavedCurrency(products: List<ProductModel>? = null): List<ProductModel>? {
        val currencyList = currencyDao.getSavedCurrencies()
        val targetCurrency = currencyList.firstOrNull { it -> it.isSelected }
        if (!products.isNullOrEmpty() && targetCurrency != null) {

            val productToNewCurrency = products.map { data ->

                data.copy(
                    price = convertPriceToAnotherCurrency(
                        data.price,
                        data.symbol,
                        targetCurrency,
                        currencyList
                    ),
                    symbol = targetCurrency.symbol
                )

            }

            return productToNewCurrency

        }
        return null
    }


    /*
    fun connection() {

        if (webSocket != null) {
            viewModelScope.launch(Dispatchers.IO + _coroutineException) {

                _hub.emit(webSocket)
                _hub.value?.on(
                    "storeStatus",
                    { result ->

                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            if (result.Status) {
                                val productNotBelongToStore =
                                    _products.value?.filter { it.storeId != result.StoreId }
                                _products.emit(productNotBelongToStore)
                            }
                        }
                    },
                    StoreStatusDto::class.java
                )

            }

        }
    }


    init {

        connection()
    }

    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (_hub.value != null)
                _hub.value!!.stop()
        }
        super.onCleared()
    }
*/
    fun getProducts(
        pageNumber: Int,
        isLoading: Boolean? = null,
        updatePageNumber: ((value: Int) -> Unit)? = null,
        updateLoadingState: ((value: Boolean) -> Unit)? = null,
        ) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (isLoading != null) updateLoadingState?.invoke(true)
            delay(500)

            when (val result = productRepository.getProduct(pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<ProductDto>

                    val holder = mutableListOf<ProductModel>()
                    val productsResponse = data.map { it.toProduct() }.toList()

                    val productWithSavedCurrency =
                        convertProductCurrencyToSavedCurrency(productsResponse) ?: productsResponse

                    holder.addAll(productWithSavedCurrency)

                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    val distinctSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinctSubCategories.isNotEmpty())
                        _products.emit(distinctSubCategories)
                    else if (_products.value == null)
                        _products.emit(emptyList())



                    if (isLoading != null) updateLoadingState?.invoke(false)
                    if (data.size == 25)
                        updatePageNumber?.invoke(pageNumber + 1)
                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)

                    if (isLoading != null) updateLoadingState?.invoke(false)
                }
            }

        }

    }

    fun getProducts(
        pageNumber: Int,
        storeId: UUID,
        isLoading: Boolean? = null,
        updatePageNumber: ((value: Int) -> Unit)? = null,
        updateLoadingState: ((value: Boolean) -> Unit)? = null,
    ) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (isLoading != null) updateLoadingState?.invoke(true)
            when (val result = productRepository.getProduct(storeId, pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<ProductDto>

                    val holder = mutableListOf<ProductModel>()
                    val productsResponse = data.map { it.toProduct() }.toList()

                    val productWithSavedCurrency =
                        convertProductCurrencyToSavedCurrency(productsResponse) ?: productsResponse

                    holder.addAll(productWithSavedCurrency)
                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    val distinctSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinctSubCategories.isNotEmpty())
                        _products.emit(distinctSubCategories)
                    else if (_products.value == null)
                        _products.emit(emptyList())

                    if (isLoading != null) updateLoadingState?.invoke(false)
                    if (data.size == 25)
                        updatePageNumber?.invoke(pageNumber + 1)

                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                    if (isLoading != null) updateLoadingState?.invoke(false)
                }
            }
        }

    }

    fun getProductsByCategoryID(
        pageNumber: Int,
        categoryId: UUID,
        isLoading: Boolean? = null,
        updatePageNumber: ((value: Int) -> Unit)? = null,
        updateLoadingState: ((value: Boolean) -> Unit)? = null,
    ) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (isLoading != null) updateLoadingState?.invoke(true)
            val result = productRepository
                .getProductByCategoryId(
                    categoryId,
                    pageNumber
                )
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<ProductDto>

                    val holder = mutableListOf<ProductModel>()

                    val productsResponse = data.map { it.toProduct() }.toList()

                    val productWithSavedCurrency =
                        convertProductCurrencyToSavedCurrency(productsResponse) ?: productsResponse

                    holder.addAll(productWithSavedCurrency)

                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    val distinctSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinctSubCategories.isNotEmpty())
                        _products.emit(distinctSubCategories)
                    else if (_products.value == null)
                        _products.emit(emptyList())

                    if (isLoading != null) updateLoadingState?.invoke(false)
                    if (data.size == 25)
                        updatePageNumber?.invoke(pageNumber + 1)

                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                    if (isLoading != null) updateLoadingState?.invoke(false)
                }
            }
        }

    }


    fun getProducts(
        pageNumber: Int,
        storeId: UUID,
        subcategoryId: UUID,
        isLoading: Boolean? = null,
        updatePageNumber: ((value: Int) -> Unit)? = null,
        updateLoadingState: ((value: Boolean) -> Unit)? = null,
    ) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (isLoading != null) updateLoadingState?.invoke(true)
            val result = productRepository.getProduct(
                storeId,
                subcategoryId,
                pageNumber = pageNumber
            )
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<ProductDto>

                    val holder = mutableListOf<ProductModel>()
                    val productsResponse = data.map { it.toProduct() }.toList()

                    val productWithSavedCurrency =
                        convertProductCurrencyToSavedCurrency(productsResponse) ?: productsResponse

                    holder.addAll(productWithSavedCurrency)


                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    val distinctSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinctSubCategories.isNotEmpty())
                        _products.emit(distinctSubCategories)
                    else if (_products.value == null)
                        _products.emit(emptyList())

                    if (isLoading != null) updateLoadingState?.invoke(false)
                    if (data.size == 25)
                        updatePageNumber?.invoke(pageNumber + 1)

                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                    if (isLoading != null) updateLoadingState?.invoke(false)

                }
            }
        }

    }

    suspend fun createProducts(
        name: String,
        description: String,
        thumbnail: File,
        subcategoryId: UUID,
        storeId: UUID,
        price: Int,
        symbol: String,
        productVariants: List<ProductVariantSelection>,
        images: List<File>
    ): String? {
        val result = productRepository.createProduct(
            name,
            description,
            thumbnail,
            subcategoryId,
            storeId,
            price,
            symbol,
            productVariants,
            images
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as ProductDto

                val holder = mutableListOf<ProductModel>()
                val addressResponse = data.toProduct()

                holder.add(addressResponse)
                if (_products.value != null) {
                    holder.addAll(_products.value!!)
                }

                if (holder.isNotEmpty())
                    _products.emit(holder)
                else
                    _products.emit(emptyList())
                return null
            }

            is NetworkCallHandler.Error -> {
                _products.emit(emptyList())

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                Log.d("errorFromGettingStoreData", errorMessage)
                return errorMessage.replace("\"", "")
            }
        }
    }

    suspend fun updateProducts(
        id: UUID,
        name: String?,
        description: String?,
        thumbnail: File?,
        subcategoryId: UUID?,
        storeId: UUID,
        price: Int?,
        symbol: String?,
        productVariants: List<ProductVariantSelection>?,
        images: List<File>?,
        deletedProductVariants: List<ProductVariantSelection>?,
        deletedImages: List<String>?

    ): String? {
        val result = productRepository.updateProduct(
            id,
            name,
            description,
            thumbnail,
            subcategoryId,
            storeId,
            price,
            symbol,
            productVariants,
            images,
            deletedProductVariants,
            deletedImages
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as ProductDto

                val holder = mutableListOf<ProductModel>()
                val addressResponse = data.toProduct()

                holder.add(addressResponse)
                if (_products.value != null) {
                    holder.addAll(_products.value!!)
                }
                val distinctHolder = holder.distinctBy { it.id }

                if (distinctHolder.isNotEmpty())
                    _products.emit(distinctHolder)

                return null
            }

            is NetworkCallHandler.Error -> {
                _products.emit(emptyList())

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                Log.d("errorFromGettingStoreData", errorMessage)
                return errorMessage.replace("\"", "")
            }
        }
    }


    suspend fun deleteProduct(storeId: UUID, productId: UUID): String? {
        val result = productRepository.deleteProduct(
            storeId = storeId,
            productId = productId
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                if (products.value != null) {
                    val copyProduct = _products.value?.filter { it.id != productId }
                    _products.emit(copyProduct)
                }
                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                Log.d("errorFromGettingStoreData", errorMessage)
                return errorMessage.replace("\"", "")
            }
        }
    }


}