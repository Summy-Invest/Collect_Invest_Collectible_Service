package com.collect.invest.entity

data class BuySellRequest(
    val collectibleId: Long,
    val userId: Long,
    val shares: Int
)
