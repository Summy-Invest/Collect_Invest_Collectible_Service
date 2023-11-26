package com.collect.invest

import com.collect.invest.entity.CollectibleItem
import com.collect.invest.entity.CollectibleRecord
import java.time.LocalDateTime

class CollectiblesManager {

    fun buyCollectible(item: CollectibleItem, userId: Int, sharesToBuy: Int) {
        if (item.availableShares >= sharesToBuy) {
            val totalPrice = sharesToBuy * item.currentPrice
            item.availableShares -= sharesToBuy
            val record = CollectibleRecord(1, LocalDateTime.now(), sharesToBuy, item.id, userId, totalPrice, null)
            //запись в бд
            //update CollectibleItem в бд

            println("Пользователь $userId приобрел $sharesToBuy долей ${item.name} за $totalPrice")
        } else {
            println("Недостаточно доступных долей для покупки.")
        }
    }

    fun sellCollectible(item: CollectibleItem, userId: Int, sharesToSell: Int) {
        val totalPrice = sharesToSell * item.currentPrice
        item.availableShares += sharesToSell
        val record = CollectibleRecord(1, LocalDateTime.now(), -sharesToSell, item.id, userId, totalPrice, null)
        //запись в бд
        //update CollectibleItem в бд

        println("Пользователь $userId продал $sharesToSell долей ${item.name} за $totalPrice")
    }

}
