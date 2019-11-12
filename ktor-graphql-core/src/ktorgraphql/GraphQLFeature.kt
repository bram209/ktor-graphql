package ktorgraphql

import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.content.defaultResource
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.AttributeKey
import java.io.File
import java.io.Reader
import java.io.StringReader


fun GraphQLFeature.Configuration.config(body: ConfigBuilder) {
    config = body
}

fun GraphQLFeature.Configuration.schemaBuilder(config: SchemaBuilderConfig.() -> GraphQLSchema) {
    schemaBuilder = { SchemaBuilderConfig().config() }
}

typealias ConfigBuilder = GraphQL.Builder.() -> Unit
typealias SchemaBuilder = () -> GraphQLSchema

open class SchemaBuilderConfig {
    open fun toSchema(): GraphQLSchema? = null
}

fun SchemaBuilderConfig.withRuntimeWiring(file: File, wiringConfig: RuntimeWiring.Builder.() -> Unit) =
    withRuntimeWiring(file.bufferedReader(), wiringConfig)

fun SchemaBuilderConfig.withRuntimeWiring(sourceInput: String, wiringConfig: RuntimeWiring.Builder.() -> Unit) =
    withRuntimeWiring(StringReader(sourceInput), wiringConfig)

fun SchemaBuilderConfig.withRuntimeWiring(
    reader: Reader,
    wiringConfig: RuntimeWiring.Builder.() -> Unit
): GraphQLSchema {
    val schemaParser = SchemaParser()
    val typeDefinitionRegistry = schemaParser.parse(reader)

    val runtimeWiring = newRuntimeWiring()
        .apply { wiringConfig() }
        .build()

    val schemaGenerator = SchemaGenerator()
    return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
}

class GraphQLFeature(configuration: Configuration) {
    val contextBuilder = configuration.contextBuilder
    val builderConfig = configuration.config
    val schema = requireNotNull(configuration.schemaBuilder).invoke()

    val graphQL: GraphQL = GraphQL.newGraphQL(schema).apply { builderConfig() }.build()

    class Configuration {
        var schemaBuilder: SchemaBuilder? = null
        var contextBuilder: (ApplicationCall) -> Any = { ApplicationCallContext(it) }
        var config: ConfigBuilder = {}
    }

    companion object Feature :
        ApplicationFeature<Application, Configuration, GraphQLFeature> {

        override val key = AttributeKey<GraphQLFeature>("GraphQLFeature")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): GraphQLFeature {
            val configuration = Configuration().apply(configure)
            val feature = GraphQLFeature(configuration)

            pipeline.routing {
                post("/graphql") {
                    call.executeGraphQLQuery()
                }

//                get("/graphql") {
//                    call.executeGraphQLQuery()
//                }

                static("/playground") {
                    defaultResource("playground.html")
//                    resources("META-INF/resources/webjars/graphql-playground-html/1.6.6/dist")

                }
            }
            return feature
        }
    }
}

data class ApplicationCallContext(val applicationCall: ApplicationCall)

