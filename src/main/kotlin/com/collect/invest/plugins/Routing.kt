package com.collect.invest.plugins

import com.collect.invest.CollectiblesManager
import com.collect.invest.entity.CollectibleItem
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {

    data class BuyRequest(val collectibleId: Long, val userId: Long, val sharesToBuy: Int)
    data class SellRequest(val collectibleId: Long, val userId: Long, val sharesToSell: Int)

    val collectiblesManager = CollectiblesManager()

    routing {

        post("/buyCollectible") {
            try {
                val buyRequest = call.receive<BuyRequest>()
                collectiblesManager.buyCollectible(buyRequest.collectibleId, buyRequest.userId, buyRequest.sharesToBuy)
                call.respond(HttpStatusCode.OK, "Покупка успешно выполнена")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Error while processing buy request")
            }
        }

        post("/sellCollectible") {
            try {
                val sellRequest = call.receive<SellRequest>()
                collectiblesManager.sellCollectible(sellRequest.collectibleId, sellRequest.userId, sellRequest.sharesToSell)
                call.respond(HttpStatusCode.OK, "Продажа успешно выполнена")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Error while processing sell request")
            }
        }

        get("/getCollectibleById/{collectibleId}") {
            try {
                val collectibleId = call.parameters["collectibleId"]?.toLongOrNull()
                val collectible = collectiblesManager.getCollectibleById(collectibleId!!)
                call.respond(HttpStatusCode.OK, collectible)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.NotFound, "Error while processing getCollectibleById request")
            }
        }

        get("/getAllCollectibles") {
            try {
                val collectibles = collectiblesManager.getAllCollectibles()
                call.respond(HttpStatusCode.OK, collectibles)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Error while processing getAllCollectibles request")
            }
        }
    }
}
