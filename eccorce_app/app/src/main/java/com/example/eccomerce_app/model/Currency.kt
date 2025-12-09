package com.example.eccomerce_app.model

import java.util.UUID

data class Currency(
    var id: UUID,
    var name: String,
    var symbol: String,
    var value: Int,
    var isDefault: Boolean
)
