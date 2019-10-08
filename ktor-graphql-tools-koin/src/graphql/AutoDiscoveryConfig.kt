package koin

import tools.AutoDiscoveryConfig
import org.koin.core.Koin
import org.koin.core.scope.Scope
import tools.instantiator

fun AutoDiscoveryConfig.koinInstantiator(koin: Koin) = koinInstantiator(koin.rootScope)

fun AutoDiscoveryConfig.koinInstantiator(koinScope: Scope) {
    instantiator(KoinInstantiator(koinScope))
}



