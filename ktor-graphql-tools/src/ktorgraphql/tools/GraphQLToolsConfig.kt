package ktorgraphql.tools

import com.coxautodev.graphql.tools.GraphQLResolver
import com.coxautodev.graphql.tools.SchemaParser
import com.coxautodev.graphql.tools.SchemaParserBuilder
import ktorgraphql.SchemaBuilderConfig
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLSchema

typealias ParserConfigBuilder = SchemaParserBuilder.() -> Unit

class KtorGraphQLToolsConfig(
    internal var parserConfig: ParserConfigBuilder = { },
    internal var autoDiscoveryConfig: AutoDiscoveryConfig? = null
)

fun KtorGraphQLToolsConfig.autoDiscovery(config: AutoDiscoveryConfig.() -> Unit) {
    autoDiscoveryConfig = AutoDiscoveryConfig().apply(config)
}

fun KtorGraphQLToolsConfig.parser(config: SchemaParserBuilder.() -> Unit) {
    parserConfig = config
}

fun SchemaBuilderConfig.withGraphQLTools(builder: KtorGraphQLToolsConfig.() -> Unit): GraphQLSchema {
    val config = KtorGraphQLToolsConfig().apply(builder)
    val parserBuilder = SchemaParser.newParser()
    val autoDiscoveryConfig = config.autoDiscoveryConfig
    if (autoDiscoveryConfig != null) {
        val packagesToScan = requireNotNull(autoDiscoveryConfig.packagesToScan)
        val dependencyInjector = requireNotNull(autoDiscoveryConfig.instantiator)
        val result = GraphQLToolsScanner.scan(packagesToScan)

        val resolvers = result.resolverClasses.map { dependencyInjector.create(it) as GraphQLResolver<*> }
        val scalars = result.scalarClasses.map { dependencyInjector.create(it) as GraphQLScalarType }
        parserBuilder.resolvers(resolvers).scalars(scalars)
    }

    val parserConfig = config.parserConfig
    return parserBuilder.apply(parserConfig).build().makeExecutableSchema()
}

