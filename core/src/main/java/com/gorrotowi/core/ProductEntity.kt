package com.gorrotowi.core

data class ProductEntity(
    val id: String,
    val productName: String,
    val productDescrp: String,
    var quantity: Int,
    val productCode: String,
    val urlImg: String,
    val price: Float
)