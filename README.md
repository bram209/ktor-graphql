# ktor-graphql

Easily integrate graphql into your ktor project through [graphql-java](https://github.com/graphql-java/graphql-java) and [graphql-java-tools](https://github.com/graphql-java-kickstart/graphql-java-tools).

This project consist of multiple modules:

* ktor-graphql-core - Provides Ktor with grahpql calls and `graphql-playgound`
* ktor-graphql-tools - Adds `graphql-java-tools` to BYOO (Bring your own object) for creating an executable `graphql-java` schema 
* ktor-graphql-tools-koin - Use the Koin dependency injector to inject services into your query resolvers 

## Examples

### ktor-graphql-core
```kotlin
    data class MyCustomContext(val applicationCall: ApplicationCall, val customHeader: String? = null)

    \\ ...

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
```

Go to: `localhost:8080/playground`

### ktor-graphql-tools
This setup will scan for all graphql query resolvers & scalars.
```kotlin
    class Query(val helloWorldService: HelloWorldService) : GraphQLQueryResolver {
        fun hello() = helloWorldService.sayHello()
    }
    
    // ...

    install(GraphQLFeature) {
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
```

Setup Koin:
```kotlin
    interface HelloWorldService {
        fun sayHello(): String
    }

    class HelloWorldServiceImpl: HelloWorldService {
        override fun sayHello(): String = "world"
    }

    //...
    
    install(Koin) {
        modules(
            module {
                single<HelloWorldService> { HelloWorldServiceImpl() }
            }
        )
    }
```