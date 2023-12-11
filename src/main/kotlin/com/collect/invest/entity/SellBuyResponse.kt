package com.collect.invest.entity

import kotlinx.serialization.Serializable

@Serializable
data class SellBuyResponse(
    val id: Long,
    val status: String
)
