package com.dropbox.kaiken.runtime

import androidx.lifecycle.ViewModelStoreOwner
import com.dropbox.kaiken.Injector
import kotlin.reflect.KClass

/**
 * A [ViewModelStoreOwner] that can hold a reference to an injector (a component in the dagger
 * world).
 */
interface InjectorHolder<InjectorType : Injector> :
    ViewModelStoreOwner, InjectorFactoryProvider<InjectorType> {

    fun locateInjector() = locateInjectorInternal()

    private fun locateInjectorInternal(): InjectorType {
        val kclass: KClass<out InjectorHolder<*>> = this::class
        var injectorFactory: InjectorFactory<InjectorType>? = null

        if (BuildConfig.DEBUG) {
            injectorFactory = KaikenRuntimeTestUtils.injectorFactoryOverrideFor(kclass)
        }

        if (injectorFactory == null) {
            injectorFactory = this.getInjectorFactory()
        }

        return locateInjector(this, injectorFactory)
    }
}
