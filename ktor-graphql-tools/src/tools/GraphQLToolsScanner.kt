package tools

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.SchemaParserDictionary
import graphql.schema.GraphQLScalarType
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import kotlin.reflect.KClass

object GraphQLToolsScanner {
    private val resolverInterfaceName = GraphQLQueryResolver::class.java.canonicalName
    private val scalarTypeInterfaceName = GraphQLScalarType::class.java.canonicalName
    private val schemaParserDictionaryInterfaceName = SchemaParserDictionary::class.java.canonicalName

    data class GraphQLScanResult(
        val resolverClasses: List<KClass<*>>,
        val scalarClasses: List<KClass<*>>,
        val parserDictionaryClasses: List<KClass<*>>
    )

    fun scan(packagesToScan: List<String>): GraphQLScanResult {

        fun ScanResult.findByInterface(interfaceName: String) =
            allClasses
                .filter { it.interfaces.any { it.name == interfaceName } }
                .map { it.loadClass().kotlin }

        ClassGraph()
            .enableAllInfo()
            .whitelistPackages(*packagesToScan.toTypedArray())
            .scan().use { scanResult ->
                return GraphQLScanResult(
                    scanResult.findByInterface(resolverInterfaceName),
                    scanResult.findByInterface(scalarTypeInterfaceName),
                    scanResult.findByInterface(schemaParserDictionaryInterfaceName)
                )
            }
    }
}
