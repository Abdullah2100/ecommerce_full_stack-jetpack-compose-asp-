package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.Room.Dao.CurrencyDao
import com.example.eccomerce_app.data.repository.CurrencyRepository
import com.example.eccomerce_app.dto.CurrencyDto
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.eccomerce_app.data.Room.Model.*

class CurrencyViewModel(
    private val currencyRepository: CurrencyRepository,
    private val scop: CoroutineScope,
    private val currencyDao: CurrencyDao
) : ViewModel() {

    private val _currencies = MutableStateFlow<List<Currency>?>(null)
    val currencies = _currencies.asStateFlow()


    val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun getCurrencies(pageNumber: Int) {
        scop.launch(Dispatchers.IO + _coroutineException) {
            val result = currencyRepository.getStoreCurrencies(pageNumber = pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<CurrencyDto>
                    _currencies.emit(null)

                    if (data.isEmpty()) {
                        val currenies = currencyDao.getSavedCurrencies()
                        if (!currenies.isNullOrEmpty()) {
                            _currencies.emit(currenies)
                        }
                        return@launch
                    }

                    val selectedCurrency = currencyDao.getSelectedCurrency();
                    currencyDao.deleteCurrencies()
                    val currencyToDbModel = data.map { data ->
                        Currency(
                            symbol = data.symbol,
                            name = data.name,
                            value = data.value,
                            isDefault = data.isDefault,
                            isSelected = selectedCurrency?.name == data.name,
                            id = null
                        )
                    }
                    currencyDao.addNewCurrency(currencyToDbModel)

                    _currencies.emit(currencyToDbModel)

                }

                is NetworkCallHandler.Error -> {
                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                }
            }
        }

    }


}