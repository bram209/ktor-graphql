package ktorgraphql.tools

import kotlin.reflect.KClass

class AutoDiscoveryConfig(
    internal var instantiator: Instantiator? = null,
    internal var packagesToScan: List<String>? = null
)

fun AutoDiscoveryConfig.instantiator(instantiator: Instantiator) {
    this.instantiator = instantiator
}

fun AutoDiscoveryConfig.instantiator(locator: (KClass<*>) -> Any) {
    instantiator = object : Instantiator {
        override fun create(kClass: KClass<*>): Any = locator(kClass)
    }
}

fun AutoDiscoveryConfig.packagesToScan(packages: List<String>) {
    packagesToScan = packages
}

