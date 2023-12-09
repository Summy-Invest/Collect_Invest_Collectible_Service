package com.collect.invest

import com.collect.invest.plugins.configureRouting
import com.collect.invest.plugins.configureSwagger
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

fun main() {
    embeddedServer(Netty, port = 3937, host = "0.0.0.0", module = Application::module)
            .start(wait = true)
}

fun Application.module() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }
    install(ContentNegotiation) {
        json(contentType = ContentType.Application.Json)
        json(contentType = ContentType.Any)
    }
    configureSwagger()
    configureRouting()
}
