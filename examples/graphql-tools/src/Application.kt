package com.github.bram209.ktorgraphql.example

import ktorgraphql.GraphQLFeature
import com.coxautodev.graphql.tools.GraphQLQueryResolver

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import ktorgraphql.config
import ktorgraphql.schemaBuilder
import ktorgraphql.tools.parser
import ktorgraphql.tools.withGraphQLTools

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

object Query : GraphQLQueryResolver {
    fun hello() = "world"
}

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
            withGraphQLTools {
                parser {
                    schemaString("type Query{hello: String}")
                    resolvers(Query)
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
