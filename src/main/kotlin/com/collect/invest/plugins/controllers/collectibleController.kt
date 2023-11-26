package com.collect.invest.plugins.controllers

import com.collect.invest.CollectiblesManager
import com.collect.invest.entity.CollectibleItem
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class BuyRequest(val item: CollectibleItem, val userId: Int, val sharesToBuy: Int)
data class SellRequest(val item: CollectibleItem, val userId: Int, val sharesToSell: Int)

fun Route.collectiblesController(collectiblesManager: CollectiblesManager) {

    post("/buyCollectible") {
        try {
            val buyRequest = call.receive<BuyRequest>()
            collectiblesManager.buyCollectible(buyRequest.item, buyRequest.userId, buyRequest.sharesToBuy)
            call.respond(HttpStatusCode.OK, "Покупка успешно выполнена")
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing buy request")
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
}

