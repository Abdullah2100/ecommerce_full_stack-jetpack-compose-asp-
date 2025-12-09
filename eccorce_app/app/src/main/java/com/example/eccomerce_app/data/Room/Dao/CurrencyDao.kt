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
    fun getSelectedCurrency():Currency?

    @Query("Select * from Currency")
    fun getSavedCurrencies(): List<Currency>


    @Insert(onConflict = REPLACE)
    fun addNewCurrency(currency: Currency)

    @Query("Update Currency set isSelected=1 where symbol=:symbol")
    fun setSelectedCurrency(symbol:String)


}