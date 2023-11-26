package com.collect.invest.entity

import java.time.LocalDateTime

data class CollectibleRecord(
    val id: Int,
    val date: LocalDateTime,
    val count: Int,
    val collectibleId: Int,
    val userId: Int,
    val totalPrice: Double,
    val transactionId: Int?
)
