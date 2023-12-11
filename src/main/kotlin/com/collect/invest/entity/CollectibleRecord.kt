package com.collect.invest.entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CollectibleRecord(
    val id: Long,
    val date: String,
    val count: Int,
    val collectibleId: Long,
    val userId: Long,
    val totalPrice: Double,
    val transactionId: Long
)
