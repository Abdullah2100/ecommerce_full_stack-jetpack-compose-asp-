package com.example.eccomerce_app.data.Room.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.eccomerce_app.data.Room.Model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Query("Select * from Currency")
    fun getSavedCurrenciesAsFlow(): Flow<List<Currency>>

    @Query("Select * from Currency where isSelected = 1")
    suspend fun getSelectedCurrency():Currency?

    @Query("Select * from Currency")
    suspend fun getSavedCurrencies(): List<Currency>


    @Insert(onConflict = REPLACE)
   suspend fun addNewCurrency(currencies: List<Currency>)

    @Query("Update Currency set isSelected=1 where symbol=:symbol")
    suspend fun setSelectedCurrency(symbol:String)
    @Query("Update Currency set isSelected=0")
    suspend fun setDeSelectCurrency()

    @Query("Delete from Currency")
    suspend fun deleteCurrencies();
}