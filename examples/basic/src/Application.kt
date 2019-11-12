package com.github.bram209.ktorgraphql.example

import ktorgraphql.GraphQLFeature
import ktorgraphql.config
import graphql.schema.StaticDataFetcher
import ktorgraphql.schemaBuilder
import ktorgraphql.withRuntimeWiring

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class MyCustomContext(val applicationCall: ApplicationCall, val customHeader: String? = null)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(GraphQLFeature) {
        contextBuilder = { MyCustomContext(it, it.request.header("customHeader")) }

        config {
            // (Optional) configure graphql builder here
        }

        schemaBuilder {
            withRuntimeWiring("type Query{hello: String}") {
                type("Query") {
                    it.dataFetcher("hello", StaticDataFetcher("world"))
                }
            }
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }
}
