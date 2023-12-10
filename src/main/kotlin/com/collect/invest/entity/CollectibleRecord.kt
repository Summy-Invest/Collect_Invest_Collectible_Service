package com.collect.invest.entity

import java.time.LocalDateTime

data class CollectibleRecord(
    val id: Long,
    val date: LocalDateTime,
    val count: Int,
    val collectibleId: Long,
    val userId: Long,
    val totalPrice: Double,
    val transactionId: Long
)
