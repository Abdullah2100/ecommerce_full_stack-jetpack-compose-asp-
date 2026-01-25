package com.example.eccomerce_app.data.Room.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.eccomerce_app.data.Room.Model.AuthModelEntity
import com.example.eccomerce_app.data.Room.Model.IsPassLocationScreen
import com.example.eccomerce_app.data.Room.Model.IsPassOnBoardingScreen

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun saveAuthData(authData: AuthModelEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun savePassingOnBoarding(value: IsPassOnBoardingScreen)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun savePassingLocation(authData: IsPassLocationScreen)


    @Query("SELECT * FROM AuthModelEntity")
    suspend fun getAuthData(): AuthModelEntity?

    @Query("SELECT COUNT(*)>0 FROM IsPassOnBoardingScreen")
    fun isPassOnBoarding(): Boolean?

    @Query("SELECT count(*)>0 FROM location ")
    suspend fun isPassLocationScreen(): Boolean


    @Query("DELETE FROM AuthModelEntity ")
    suspend fun nukeAuthTable()

    @Query("DELETE FROM location ")
    suspend fun nukeIsPassAddressTable()

}