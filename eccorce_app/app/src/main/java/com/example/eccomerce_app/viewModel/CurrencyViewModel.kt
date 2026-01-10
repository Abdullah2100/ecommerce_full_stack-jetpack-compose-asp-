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
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CurrencyViewModel(
    private val currencyRepository: CurrencyRepository,
    private val scop: CoroutineScope,
    private val currencyDao: CurrencyDao
) : ViewModel() {


    val selectedCurrency = currencyDao
        .getSelectedCurrencyFlow()
        .stateIn(
            scop,
            started = SharingStarted.WhileSubscribed(2000L),
            initialValue = null
        )

    val currenciesList = currencyDao
        .getSavedCurrenciesAsFlow()
        .stateIn(
            scop,
            started = SharingStarted.WhileSubscribed(2000L),
            initialValue = null
        )


    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun getCurrencies(pageNumber: Int) {
        scop.launch(Dispatchers.IO + _coroutineException) {
            when (val result = currencyRepository.getStoreCurrencies(pageNumber = pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<CurrencyDto>

                    if (data.isEmpty()) {
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