package com.collect.invest.entity

data class SellRequest(
    val collectibleId: Long,
    val userId: Long,
    val sharesToSell: Int
)
