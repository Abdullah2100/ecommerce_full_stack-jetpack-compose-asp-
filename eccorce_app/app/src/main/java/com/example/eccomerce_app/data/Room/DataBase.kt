package com.example.eccomerce_app.data.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.eccomerce_app.data.Room.Dao.AuthDao
import com.example.eccomerce_app.data.Room.Dao.CurrencyDao
import com.example.eccomerce_app.data.Room.Dao.LocaleDao
import com.example.eccomerce_app.data.Room.Model.IsPassLocationScreen
import com.example.eccomerce_app.data.Room.Model.AuthModelEntity
import com.example.eccomerce_app.data.Room.Model.Currency
import com.example.eccomerce_app.data.Room.Model.CurrentLocal
import com.example.eccomerce_app.data.Room.Model.IsPassOnBoardingScreen

@Database(
    entities = [
        AuthModelEntity::class,
        IsPassOnBoardingScreen::class,
        IsPassLocationScreen::class,
        CurrentLocal::class,
        Currency::class
    ], version = 1, exportSchema = false
)
abstract class DataBase
    : RoomDatabase()
{
    abstract fun authDao(): AuthDao
    abstract fun currentLocal(): LocaleDao
    abstract fun currencyDao(): CurrencyDao
}