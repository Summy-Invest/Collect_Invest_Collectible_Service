package com.collect.invest

import com.collect.invest.entity.CollectibleItem
import com.collect.invest.entity.CollectibleRecord
import com.collect.invest.entity.SellBuyResponse
import com.collect.invest.entity.UpdateResponse
import com.collect.invest.utils.HttpClientFactory
import io.ktor.http.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.lang.Exception
import java.time.LocalDateTime

class CollectiblesManager {

    suspend fun buyCollectible(item: CollectibleItem, userId: Long, sharesToBuy: Int) {
        if (item.availableShares >= sharesToBuy) {
            val totalPrice = sharesToBuy * item.currentPrice * 1.05
            item.availableShares -= sharesToBuy

            HttpClientFactory.createHttpClient().use { client ->
                val buyResponse = client.post("http://localhost:8080/buy/$userId/$totalPrice")
                if (buyResponse.status != HttpStatusCode.OK) {
                    throw Exception("Error while processing buy request")
                } else {
                    val transactionId = buyResponse.body<SellBuyResponse>().id
                    val record = CollectibleRecord(1, LocalDateTime.now(), sharesToBuy, item.id, userId, totalPrice, transactionId)
                    //Создание записи портфолио
                    val createRecResponse = client.post("http://localhost:8080/collectableService/??"){
                        contentType(ContentType.Application.Json)
                        setBody(record)
                    }
                }
            }

            val updResp = UpdateResponse(item.id, item.availableShares)
            HttpClientFactory.createHttpClient().use { client ->
                // Обновление коллекционки
                val updateResponse =
                    client.post("http://localhost:8080/collectableService/collectable/updateCollectable"){
                        contentType(ContentType.Application.Json)
                        setBody(updResp)
                    }
                if (updateResponse.status != HttpStatusCode.OK) {
                    throw Exception("Error while updating collectable")
                }
            }
        } else {
            throw Exception("Недостаточно доступных долей для покупки.")
        }
    }


    suspend fun sellCollectible(item: CollectibleItem, userId: Long, sharesToSell: Int) {
        //проверка наличия долей у пользователя
        val totalPrice = sharesToSell * item.currentPrice * 0.95
        item.availableShares += sharesToSell

        HttpClientFactory.createHttpClient().use { client ->
            val sellResponse = client.post("http://localhost:8080/sell/$userId/$totalPrice")
            if (sellResponse.status != HttpStatusCode.OK){
                throw Exception("Error while processing buy request")
            } else {
            val transactionId = sellResponse.body<SellBuyResponse>().id
            val record = CollectibleRecord(1, LocalDateTime.now(), sharesToSell, item.id, userId, totalPrice, transactionId)
            //Создание записи портфолио
            val createRecResponse = client.post("http://localhost:8080/collectableService/??"){
                contentType(ContentType.Application.Json)
                setBody(record)
            }
        }
        }

        val updResp = UpdateResponse(item.id, item.availableShares)
        HttpClientFactory.createHttpClient().use { client ->
            val updateResponse =
                client.post("http://localhost:8080/collectableService/collectable/updateCollectableById/${item.id}"){
                    contentType(ContentType.Application.Json)
                    setBody(updResp)
                }
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
    }
}
