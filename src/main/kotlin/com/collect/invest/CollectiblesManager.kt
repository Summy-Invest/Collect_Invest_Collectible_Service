package com.collect.invest

import com.collect.invest.entity.CollectibleItem
import com.collect.invest.entity.CollectibleRecord
import com.collect.invest.entity.SellBuyResponse
import com.collect.invest.entity.UpdateResponse
import com.collect.invest.utils.HttpClientSingleton
import io.ktor.http.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.time.LocalDateTime

class CollectiblesManager {
    private val db = "http://localhost:8080"
    private val financial = "http://localhost:7777"

    suspend fun buyCollectible(item: CollectibleItem, userId: Long, sharesToBuy: Int) {
        //TODO переделать item в просто id коллекционки и добавить priceChangeBuy/priceChangeSell для соответсвующих
        // методов (методы реализованы в конце файла)
        if (item.availableShares >= sharesToBuy) {
            val totalPrice = sharesToBuy * item.currentPrice * 1.05
            item.availableShares -= sharesToBuy

            val client = HttpClientSingleton.client
            val buyResponse = client.post("$financial/buy/$userId/$totalPrice")
            when (buyResponse.status) {
                HttpStatusCode.OK -> {
                    if (buyResponse.body<SellBuyResponse>().status == "success") {
                        val updResp = UpdateResponse(item.id, item.availableShares)
                        // Обновление количества доступных акций
                        val updateResponse =
                            client.post("$db/collectableService/collectable/updateCollectable") {
                                contentType(ContentType.Application.Json)
                                setBody(updResp)
                            }
                        if (updateResponse.status != HttpStatusCode.OK) {
                            throw Exception("Error while updating collectable")
                        }

                    }
                    else {
                        throw Exception("transaction is unsuccessful")
                    }
                }
                else -> {
                    throw Exception("financial service unavailable")
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

        val client = HttpClientSingleton.client
        val sellResponse = client.post("$db/sell/$userId/$totalPrice")
        when(sellResponse.status) {
            HttpStatusCode.OK -> {
                val transactionId = sellResponse.body<SellBuyResponse>().id
                val record =
                    CollectibleRecord(1, LocalDateTime.now(), sharesToSell, item.id, userId, totalPrice, transactionId)
                runBlocking {
                    //Создание записи портфолио
                    val createRecResponse = async {
                        client.post("$db/collectableService/collectable/addPortfolioRecord") {
                            contentType(ContentType.Application.Json)
                            setBody(record)
                        }
                    }

                    val updResp = UpdateResponse(item.id, item.availableShares)
                    val updateResponse = async {
                        client.post("$db/collectableService/collectable/updateCollectableById/${item.id}") {
                            contentType(ContentType.Application.Json)
                            setBody(updResp)
                        }
                    }

                    when (createRecResponse.await().status) {
                        HttpStatusCode.OK -> {

                        }

                        else -> {
                            throw Exception("Error while adding portfolio record")
                        }
                    }

                    when (updateResponse.await().status) {
                        HttpStatusCode.OK -> {

                        }

                        else -> {
                            throw Exception("Error while updating collectable")
                        }
                    }
                }
            }

            else -> {
                throw Exception("Error while processing buy request")
            }
        }
    }

    suspend fun getCollectibleById(collectibleId: Long): CollectibleItem {
        // запрос бд
        val client = HttpClientSingleton.client
        val response = client.get("$db/collectableService/collectable/getCollectableById/$collectibleId")
        when (response.status){
            HttpStatusCode.OK -> {
                return response.body<CollectibleItem>()
            }

            else -> {
                throw Exception("Error while getCollectibleById")
            }
        }
    }

    suspend fun getAllCollectibles(): List<CollectibleItem> {
        // запрос бд
        val client = HttpClientSingleton.client
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

    private fun priceChangeBuy(sharesToBuy: Int, price: Double) = price + price * 0.01 * sharesToBuy
    private fun  priceChangeSell(sharesToSell: Int, price: Double) = price - price * 0.01 * sharesToSell
}
