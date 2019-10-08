package com.github.bram209.ktorgraphql.example

import com.coxautodev.graphql.tools.GraphQLQueryResolver

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.*
import com.fasterxml.jackson.databind.*
import graphql.*
import io.ktor.jackson.*
import koin.koinInstantiator
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.getKoin
import tools.autoDiscovery
import tools.packagesToScan
import tools.parser
import tools.withGraphQLTools

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

interface HelloWorldService {
    fun sayHello(): String
}

class HelloWorldServiceImpl: HelloWorldService {
    override fun sayHello(): String = "world"
}

class Query(val helloWorldService: HelloWorldService) : GraphQLQueryResolver {
    fun hello() = helloWorldService.sayHello()
}

data class MyCustomContext(val applicationCall: ApplicationCall, val customHeader: String? = null)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    
    install(Koin) {
        modules(
            module {
                single<HelloWorldService> { HelloWorldServiceImpl() }
            }
        )
    }

    install(GraphQLFeature) {
        contextBuilder = { MyCustomContext(it, it.request.header("customHeader")) }

        config {
            // (Optional) configure graphql builder here
        }

        schemaBuilder {
            withGraphQLTools {
                autoDiscovery {
                    packagesToScan(listOf("com.github.bram209"))
                    koinInstantiator(getKoin())
                }

                parser {
                    schemaString("type Query{hello: String}")
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
