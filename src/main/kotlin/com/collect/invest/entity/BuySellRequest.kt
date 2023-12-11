package com.collect.invest.entity

import kotlinx.serialization.Serializable

@Serializable
data class BuySellRequest(
    val collectibleId: Long,
    val userId: Long,
    val shares: Int
)
