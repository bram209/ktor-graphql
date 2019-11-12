package ktorgraphql.tools.koin

import ktorgraphql.tools.AutoDiscoveryConfig
import org.koin.core.Koin
import org.koin.core.scope.Scope
import ktorgraphql.tools.instantiator

fun AutoDiscoveryConfig.koinInstantiator(koin: Koin) = koinInstantiator(koin.rootScope)

fun AutoDiscoveryConfig.koinInstantiator(koinScope: Scope) {
    instantiator(KoinInstantiator(koinScope))
}



