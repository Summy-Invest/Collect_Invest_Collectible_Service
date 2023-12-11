package com.collect.invest.entity

import kotlinx.serialization.Serializable


@Serializable
data class UpdateCollectiblePrice(
    val id: Long,
    val currentPrice: Double,
)
