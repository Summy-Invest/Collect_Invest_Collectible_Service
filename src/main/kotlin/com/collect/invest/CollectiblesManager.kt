package com.collect.invest

import com.collect.invest.entity.CollectibleItem
import com.collect.invest.utils.HttpClientFactory
import io.ktor.http.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.lang.Exception

class CollectiblesManager {

    suspend fun buyCollectible(item: CollectibleItem, userId: Int, sharesToBuy: Int) {
        if (item.availableShares >= sharesToBuy) {
            val totalPrice = sharesToBuy * item.currentPrice * 1.05
            item.availableShares -= sharesToBuy

            HttpClientFactory.createHttpClient().use { client ->
                val buyResponse = client.post("http://localhost:8080/buy/$userId/$totalPrice")
                if (buyResponse.status != HttpStatusCode.OK) {
                    throw Exception("Error while processing buy request")
                }
            }

            HttpClientFactory.createHttpClient().use { client ->
                val updateResponse =
                    client.post("http://localhost:8080/collectableService/collectable/updateCollectableById/${item.id}")
                if (updateResponse.status != HttpStatusCode.OK) {
                    throw Exception("Error while updating collectable")
                }
            }
        } else {
            throw Exception("Недостаточно доступных долей для покупки.")
        }
    }


    suspend fun sellCollectible(item: CollectibleItem, userId: Int, sharesToSell: Int) {
        val totalPrice = sharesToSell * item.currentPrice * 0.95
        item.availableShares += sharesToSell

        HttpClientFactory.createHttpClient().use { client ->
            val sellResponse = client.post("http://localhost:8080/sell/$userId/$totalPrice")
            if (sellResponse.status != HttpStatusCode.OK) {
                throw Exception("Error while processing buy request")
            }
        }

        HttpClientFactory.createHttpClient().use { client ->
            val updateResponse =
                client.post("http://localhost:8080/collectableService/collectable/updateCollectableById/${item.id}")
            if (updateResponse.status != HttpStatusCode.OK) {
                throw Exception("Error while updating collectable")
            }
        }

    }

    suspend fun getCollectibleById(collectibleId: Long): CollectibleItem {
        // запрос бд
        HttpClientFactory.createHttpClient().use { client ->
            val response = client.get("http://localhost:8080/collectableService/collectable/getCollectableById/$collectibleId")
            when (response.status){
                HttpStatusCode.OK -> {
                    return response.body<CollectibleItem>()
                }

                else -> {
                    throw Exception("Error while getCollectibleById")
                }
            }
        }
    }

    suspend fun getAllCollectibles(): List<CollectibleItem> {
        // запрос бд
        HttpClientFactory.createHttpClient().use { client ->
            val response = client.get("http://localhost:8080/collectableService/collectable/getAllCollectibles")
            when (response.status){
                HttpStatusCode.OK -> {
                    return response.body<List<CollectibleItem>>()
                }

                else -> {
                    throw Exception("Error while getAllCollectibles")
                }
            }
        }
//        return listOf(
//            CollectibleItem(1, "Item1", "Description1", "Category1", "PhotoLink1", 50.0, 5),
//            CollectibleItem(2, "Item2", "Description2", "Category2", "PhotoLink2", 75.0, 8)
//        )
    }

}
