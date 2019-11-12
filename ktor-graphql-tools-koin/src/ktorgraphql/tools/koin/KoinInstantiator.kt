package ktorgraphql.tools.koin

import ktorgraphql.tools.Instantiator
import org.koin.core.Koin
import org.koin.core.scope.Scope
import org.koin.experimental.builder.getArguments
import org.koin.experimental.builder.getFirstJavaConstructor
import org.koin.experimental.builder.makeInstance
import kotlin.reflect.KClass

class KoinInstantiator(val koinScope: Scope) : Instantiator {
    constructor(koin: Koin): this(koin.rootScope)

    override fun create(kClass: KClass<*>): Any {
        val ctor = kClass.getFirstJavaConstructor()
        val args = getArguments(ctor, koinScope)
        return ctor.makeInstance(args)
    }
}