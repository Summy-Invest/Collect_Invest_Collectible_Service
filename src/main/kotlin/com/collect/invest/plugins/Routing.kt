package com.collect.invest.plugins

import com.collect.invest.CollectiblesManager
import com.collect.invest.entity.CollectibleItem
import com.collect.invest.plugins.controllers.AllCollectiblesResponse
import com.collect.invest.plugins.controllers.BuyRequest
import com.collect.invest.plugins.controllers.CollectibleResponse
import com.collect.invest.plugins.controllers.SellRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {

    data class BuyRequest(val item: CollectibleItem, val userId: Int, val sharesToBuy: Int)
    data class SellRequest(val item: CollectibleItem, val userId: Int, val sharesToSell: Int)
    data class CollectibleResponse(val collectible: CollectibleItem?)
    data class AllCollectiblesResponse(val collectibles: List<CollectibleItem>)

    val collectiblesManager = CollectiblesManager()

    routing {

        post("/buyCollectible") {
            try {
                val buyRequest = call.receive<BuyRequest>()
                collectiblesManager.buyCollectible(buyRequest.item, buyRequest.userId, buyRequest.sharesToBuy)
                call.respond(HttpStatusCode.OK, "Покупка успешно выполнена")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Error while processing buy request")
            }
        }

        post("/sellCollectible") {
            try {
                val sellRequest = call.receive<SellRequest>()
                collectiblesManager.sellCollectible(sellRequest.item, sellRequest.userId, sellRequest.sharesToSell)
                call.respond(HttpStatusCode.OK, "Продажа успешно выполнена")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Error while processing sell request")
            }
        }

        get("/getCollectibleById/{collectibleId}") {
            try {
                val collectibleId = call.parameters["collectibleId"]?.toLongOrNull()
                val collectible = collectibleId?.let { collectiblesManager.getCollectibleById(collectibleId) }
                call.respond(HttpStatusCode.OK, CollectibleResponse(collectible))
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.NotFound, "Error while processing getCollectibleById request")
            }
        }

        get("/getAllCollectibles") {
            try {
                val collectibles = collectiblesManager.getAllCollectibles()
                call.respond(HttpStatusCode.OK, AllCollectiblesResponse(collectibles))
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Error while processing getAllCollectibles request")
            }
        }
    }
}
