package com.collect.invest.plugins

import com.collect.invest.CollectiblesManager
import com.collect.invest.entity.BuySellRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {


    val collectiblesManager = CollectiblesManager()

    routing {

        post("/buyCollectible") {
            try {
                val buyRequest = call.receive<BuySellRequest>()
                collectiblesManager.buyCollectible(buyRequest.collectibleId, buyRequest.userId, buyRequest.shares)
                call.respond(HttpStatusCode.OK, "Покупка успешно выполнена")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, e.toString())
            }
        }

        post("/sellCollectible") {
            try {
                val sellRequest = call.receive<BuySellRequest>()
                collectiblesManager.sellCollectible(sellRequest.collectibleId, sellRequest.userId, sellRequest.shares)
                call.respond(HttpStatusCode.OK, "Продажа успешно выполнена")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, e.toString())
            }
        }

        get("/getCollectibleById/{collectibleId}") {
            try {
                val collectibleId = call.parameters["collectibleId"]?.toLongOrNull()
                val collectible = collectiblesManager.getCollectibleById(collectibleId!!)
                call.respond(HttpStatusCode.OK, collectible)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.NotFound, e.toString())
            }
        }

        get("/getAllCollectibles") {
            try {
                val collectibles = collectiblesManager.getAllCollectibles()
                call.respond(HttpStatusCode.OK, collectibles)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, e.toString())
            }
        }

        get("/getAllUserCollectibles/{userId}") {
            try {
                val userId = call.parameters["userId"]!!.toLong()
                val collectibles = collectiblesManager.getAllUserCollectibles(userId)
                call.respond(HttpStatusCode.OK, collectibles)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, e.toString())
            }
        }

        get("/getUserCollectibles/{userId}/{collectibleId}") {
            try {
                val userId = call.parameters["userId"]!!.toLong()
                val collectibleId = call.parameters["collectibleId"]!!.toLong()
                val shares = collectiblesManager.getUserCollectible(userId, collectibleId)
                call.respond(HttpStatusCode.OK, shares)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, e.toString())
            }
        }


    }
}
