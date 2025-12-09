package com.example.eccomerce_app.data.Room.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eccomerce_app.data.Room.Model.CurrentLocal

@Dao
interface LocaleDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun saveCurrentLocale(currentLocal: CurrentLocal)


    @Query("SELECT * FROM currentlocal where id=0")
    suspend fun getCurrentLocal(): CurrentLocal?
}