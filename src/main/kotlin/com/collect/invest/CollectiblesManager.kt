package com.collect.invest

import com.collect.invest.entity.*
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


    suspend fun buyCollectible(collectibleId: Long, userId: Long, sharesToBuy: Int) {
        val item = getCollectibleById(collectibleId)
        if (item.availableShares >= sharesToBuy) {
            val totalPrice = sharesToBuy * item.currentPrice * 1.05
            item.availableShares -= sharesToBuy

            val client = HttpClientSingleton.client
            val buyResponse = client.post("$financial/buy/$userId/$totalPrice")
            when (buyResponse.status) {
                HttpStatusCode.OK -> {
                    val transaction = buyResponse.body<SellBuyResponse>()
                    val transactionId = transaction.id
                    if (transaction.status == "success") {
                        val record =
                            CollectibleRecord(
                                1,
                                LocalDateTime.now().toString(),
                                sharesToBuy,
                                item.id,
                                userId,
                                totalPrice,
                                transactionId
                            )
                        runBlocking {
                            val addPortfolioRecordResponse = async {
                                client.post("$db/collectableService/collectable/addPortfolioRecord") {
                                    contentType(ContentType.Application.Json)
                                    setBody(record)
                                }
                            }

                            val updateAvailableShares = UpdateAvailableShares(item.id, item.availableShares)
                            val updateAvailableSharesResponse = async {
                                client.patch("$db/collectableService/collectable/updateCollectableById") {
                                    contentType(ContentType.Application.Json)
                                    setBody(updateAvailableShares)
                                }
                            }

                            when (addPortfolioRecordResponse.await().status) {
                                HttpStatusCode.OK -> {

                                }

                                else -> {
                                    throw Exception("Error while adding portfolio record " +
                                            "${addPortfolioRecordResponse.await().status}")
                                }
                            }

                            when (updateAvailableSharesResponse.await().status) {
                                HttpStatusCode.OK -> {

                                }

                                else -> {
                                    throw Exception("Error while updating collectable " +
                                            "${updateAvailableSharesResponse.await().status}")
                                }
                            }
                        }
                        updatePrice(collectibleId, priceChangeBuy(sharesToBuy, item.currentPrice))

                    }

                    //если статус транзакции был не success
                    else {
                        throw Exception("Transaction is unsuccessful")
                    }

                }

                //если не удалось получить ответ от Financial Service
                else -> {
                    throw Exception("Financial service unavailable ${buyResponse.status}")
                }
            }

        }

        //если нет столько долей в доступе
        else {
            throw Exception("Недостаточно доступных долей для покупки.")
        }
    }


    suspend fun sellCollectible(collectibleId: Long, userId: Long, sharesToSell: Int) {
        val item = getCollectibleById(collectibleId)
        val client = HttpClientSingleton.client

        val availableSharesResponse =
            client.get("$db/collectableService/collectable/getUserCollectibles/$userId/$collectibleId")
        when (availableSharesResponse.status) {
            HttpStatusCode.OK -> {
                val userShares = availableSharesResponse.body<UserShares>().shares
                if (userShares >= sharesToSell) {
                    val totalPrice = sharesToSell * item.currentPrice * 0.95
                    item.availableShares += sharesToSell

                    val sellResponse = client.post("$financial/sell/$userId/$totalPrice")
                    when (sellResponse.status) {
                        HttpStatusCode.OK -> {
                            val transaction = sellResponse.body<SellBuyResponse>()
                            val transactionId = transaction.id
                            if (transaction.status == "success") {
                                val record =
                                    CollectibleRecord(
                                        1,
                                        LocalDateTime.now().toString(),
                                        0 - sharesToSell,
                                        item.id,
                                        userId,
                                        totalPrice,
                                        transactionId
                                    )
                                runBlocking {
                                    //Создание записи портфолио
                                    val addPortfolioRecordResponse = async {
                                        client.post("$db/collectableService/collectable/addPortfolioRecord") {
                                            contentType(ContentType.Application.Json)
                                            setBody(record)
                                        }
                                    }

                                    val updateAvailableShares = UpdateAvailableShares(item.id, item.availableShares)
                                    val updateAvailableSharesResponse = async {
                                        client.patch("$db/collectableService/collectable/updateCollectableById") {
                                            contentType(ContentType.Application.Json)
                                            setBody(updateAvailableShares)
                                        }
                                    }

                                    when (addPortfolioRecordResponse.await().status) {
                                        HttpStatusCode.OK -> {

                                        }

                                        else -> {
                                            throw Exception("Error while adding portfolio record " +
                                                    "${addPortfolioRecordResponse.await().status}}")
                                        }
                                    }

                                    when (updateAvailableSharesResponse.await().status) {
                                        HttpStatusCode.OK -> {

                                        }

                                        else -> {
                                            throw Exception("Error while updating collectable " +
                                                    "${updateAvailableSharesResponse.await().status}")
                                        }
                                    }
                                }
                                updatePrice(collectibleId, priceChangeSell(sharesToSell, item.currentPrice))
                            }

                            //если статус транзакции был не success
                            else {
                                throw Exception("Transaction is unsuccessful")
                            }
                        }

                        //если не удалось получить ответ от Financial Service
                        else -> {
                            throw Exception("Financial service unavailable ${sellResponse.status}")
                        }
                    }
                }

                //если у пользователя недостаточно акций для продажи
                else {
                    throw Exception("The user does not have enough shares")
                }
            }

            //если не удалось получить доступное у пользователя количество коллекционок
            else -> {
                throw Exception("Error while getting user available shares amount ${availableSharesResponse.status}")
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
                throw Exception("Error while getCollectibleById ${response.status}")
            }
        }
    }

    suspend fun getAllCollectibles(): List<CollectibleItem> {
        // запрос бд
        val client = HttpClientSingleton.client
        val response = client.get("$db/collectableService/collectable/getAllCollectibles")
        when (response.status){
            HttpStatusCode.OK -> {
                return response.body<List<CollectibleItem>>()
            }

            else -> {
                throw Exception("Error while getAllCollectibles ${response.status}")
            }
        }
    }

    suspend fun getAllUserCollectibles(userId: Long) : List<CollectibleItem> {
        val client = HttpClientSingleton.client
        val response = client.get("$db//collectableService/collectable/getAllUserCollectibles/$userId")
        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<List<CollectibleItem>>()
            }

            else -> {
                throw Exception("Error while getAllUserCollectibles ${response.status}")
            }
        }
    }

//    private suspend fun getPrice(collectibleId: Long): Double{
//        val client = HttpClientSingleton.client
//        val response = client.get("$db/collectableService/collectable/getPrice/$collectibleId")
//        when (response.status){
//            HttpStatusCode.OK -> {
//                return response.body<CollectiblePrice>().currentPrice
//            }
//
//            else -> {
//                throw Exception("Error while getting current collectible price")
//            }
//        }
//    }

    private suspend fun updatePrice(collectibleId: Long, newPrice: Double){
        val client = HttpClientSingleton.client
        val response = client.patch("$db/collectableService/collectable/updatePrice"){
            contentType(ContentType.Application.Json)
            setBody(UpdateCollectiblePrice(id = collectibleId, currentPrice = newPrice))
        }
        when (response.status){
            HttpStatusCode.OK -> {
                return
            }

            else -> {
                throw Exception("Error while updating collectible price ${response.status}")
            }
        }
    }

    private fun priceChangeBuy(sharesToBuy: Int, price: Double) = price + price * 0.01 * sharesToBuy
    private fun  priceChangeSell(sharesToSell: Int, price: Double) = price - price * 0.01 * sharesToSell
}
