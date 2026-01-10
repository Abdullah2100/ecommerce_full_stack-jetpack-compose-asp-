package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.dto.ModelToDto.toSubCategoryUpdateDto
import com.example.eccomerce_app.dto.SubCategoryDto
import com.example.eccomerce_app.model.DtoToModel.toSubCategory
import com.example.e_commercompose.model.SubCategory
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.eccomerce_app.dto.CreateSubCategoryDto
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.SubCategoryRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class SubCategoryViewModel(val subCategoryRepository: SubCategoryRepository) : ViewModel() {

   private  val _SubCategories = MutableStateFlow<List<SubCategory>?>(null)
    val subCategories = _SubCategories.asStateFlow()

    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    suspend fun createSubCategory(name: String, categoryId: UUID): String? {

        val result = subCategoryRepository.createSubCategory(
            CreateSubCategoryDto(
                Name = name, CategoryId = categoryId
            )
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as SubCategoryDto
                val listSubCategory = mutableListOf<SubCategory>()

                listSubCategory.add(data.toSubCategory())
                if (_SubCategories.value != null) {
                    listSubCategory.addAll(_SubCategories.value!!)
                }

                _SubCategories.emit(listSubCategory)

                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

        }
    }

    suspend fun updateSubCategory(
        data: SubCategoryUpdate
    ): String? {

        val result = subCategoryRepository.updateSubCategory(
            data.toSubCategoryUpdateDto()
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as SubCategoryDto
                val listSubCategory = mutableListOf<SubCategory>()

                if (_SubCategories.value != null) {
                    listSubCategory.addAll(_SubCategories.value!!.filter { it.id != data.id })
                }

                listSubCategory.add(data.toSubCategory())


                val distinctSubCategories = listSubCategory.distinctBy { it.id }.toList()

                _SubCategories.update { distinctSubCategories }

                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

        }
    }


    suspend fun deleteSubCategory(
        id: UUID
    ): String? {

        val result = subCategoryRepository.deleteSubCategory(
            id
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val filteredCategory = _SubCategories.value?.filter { it.id != id }
                _SubCategories.emit(filteredCategory)
                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

        }
    }


    fun getStoreSubCategories(storeId: UUID, pageNumber: Int = 1) {
        viewModelScope.launch(Dispatchers.Main + _coroutineException) {
            when (val result = subCategoryRepository.getStoreSubCategory(storeId, pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<SubCategoryDto>

                    val subCategoriesHolder = mutableListOf<SubCategory>()
                    val addressResponse = data.map { it.toSubCategory() }.toList()

                    subCategoriesHolder.addAll(addressResponse)
                    if (_SubCategories.value != null) {
                        subCategoriesHolder.addAll(_SubCategories.value!!)
                    }

                    val distinctSubCategories =
                        subCategoriesHolder.distinctBy { it.id }.toMutableList()

                    if (distinctSubCategories.isNotEmpty()) _SubCategories.emit(
                        distinctSubCategories
                    )

                }

                is NetworkCallHandler.Error -> {

                    if (_SubCategories.value == null) _SubCategories.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                }
            }
        }

    }


}

