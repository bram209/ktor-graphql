package tools

import kotlin.reflect.KClass

interface Instantiator {
    fun create(kClass: KClass<*>): Any
}

class SimpleInstantiator : Instantiator {
    override fun create(kClass: KClass<*>): Any {
        val objectInstance = kClass.objectInstance
        if (objectInstance != null) return objectInstance

        val noArgConstructor = kClass.java.getConstructor()
        checkNotNull(noArgConstructor) {
            "The default simple instantiator only supports instantiating no arg constructors." +
                    "Utilize a dependency injection framework instead"
        }

        return noArgConstructor.newInstance()
    }
}

