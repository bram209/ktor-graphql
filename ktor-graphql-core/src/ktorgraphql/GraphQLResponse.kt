package ktorgraphql

import com.fasterxml.jackson.annotation.JsonInclude
import graphql.ExecutionResult
import graphql.GraphQLError

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GraphQLResponse(
    val data: Any? = null,
    val errors: List<GraphQLError>? = null,
    val extensions: Map<Any, Any>? = null
)

fun ExecutionResult.toGraphQLResponse(): GraphQLResponse {
    val filteredErrors = if (errors?.isNotEmpty() == true) errors else null
    val filteredExtensions = if (extensions?.isNotEmpty() == true) extensions else null
    return GraphQLResponse(getData(), filteredErrors, filteredExtensions)
}

