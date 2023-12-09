package com.collect.invest

import com.collect.invest.entity.CollectibleItem
import com.collect.invest.entity.CollectibleRecord
import com.collect.invest.utils.HttpClientFactory
import io.ktor.http.*
import java.time.LocalDateTime
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.lang.Exception

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

    suspend fun getCollectibleById(collectibleId: Long): CollectibleItem {
        // запрос бд
        HttpClientFactory.createHttpClient().use { client ->
            val response = client.get(""){
                contentType(ContentType.Application.Json)
                setBody(collectibleId)
            }
            when (response.status){
                HttpStatusCode.OK -> {
                    return response.body<CollectibleItem>()
                }

                else -> {
                    throw Exception("Error while getCollectibleById")
                }
            }
        }


//        return CollectibleItem(
//            id = 1,
//            name = "Mocked Item",
//            description = "Mocked Description",
//            category = "Mocked Category",
//            photoLink = "Mocked Photo Link",
//            currentPrice = 100.0,
//            availableShares = 10
//        )
    }

    fun getAllCollectibles(): List<CollectibleItem> {
        // запрос бд
        return listOf(
            CollectibleItem(1, "Item1", "Description1", "Category1", "PhotoLink1", 50.0, 5),
            CollectibleItem(2, "Item2", "Description2", "Category2", "PhotoLink2", 75.0, 8)
        )
    }

}
