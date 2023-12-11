package com.collect.invest.entity

import kotlinx.serialization.Serializable


@Serializable
data class UpdateAvailableShares(
    val id: Long,
    val availableShares: Int
)
