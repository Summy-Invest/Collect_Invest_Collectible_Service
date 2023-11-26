package com.collect.invest.entity

data class CollectibleItem(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val photoLink: String,
    val currentPrice: Double,
    var availableShares: Int
)
