package com.example.eccomerce_app.data.Room.Model

import com.example.eccomerce_app.model.*
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Currency")
data class Currency(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    val name: String,
    val symbol: String,
    val value:Int,
    val isDefault:Boolean,
    val isSelected: Boolean,
)