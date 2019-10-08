package graphql

import io.ktor.application.ApplicationCall
import io.ktor.application.feature
import io.ktor.request.receive
import io.ktor.response.respond

suspend fun ApplicationCall.executeGraphQLQuery() {
    val feature = this.application.feature(GraphQLFeature)
    val request = receive<GraphQLRequest>()
    val executionInput = ExecutionInput.newExecutionInput()
        .context(feature.contextBuilder(this))
        .query(request.query)
        .operationName(request.operationName)
        .variables(request.variables ?: emptyMap())
        .build()

    respond(feature.graphQL.execute(executionInput).toGraphQLResponse())
}