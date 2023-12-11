package com.collect.invest.entity

data class BuyRequest(
    val collectibleId: Long,
    val userId: Long,
    val sharesToBuy: Int
)
