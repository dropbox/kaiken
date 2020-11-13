package com.dropbox.kaiken.runtime

import com.dropbox.kaiken.Injector
import kotlin.reflect.KClass

object Kaiken {
    private val injectorFactoryOverrides = mutableMapOf<KClass<out InjectorHolder<*>>, InjectorFactory<*>>()

    internal inline fun <reified InjectorFactoryType : InjectorFactory<*>> injectorFactoryOverrideFor(
        injectorHolderKClass: KClass<out InjectorHolder<*>>
    ): InjectorFactoryType? = injectorFactoryOverrides
        .filter { entry ->
            entry.key == injectorHolderKClass
        }
        .map { entry ->
            entry.value as InjectorFactoryType
        }
        .singleOrNull()

    /**
     * Overrides the resolution of the [InjectorFactory] for a given [InjectorHolder].
     *
     * In production, the [InjectorFactoryProvider] is used to resolve the [InjectorFactory] for a given
     * [InjectorHolder]. During testing it might be convenient for the [InjectorFactory] to be resolved
     * by a different provider, for example, in case a mock injector needs to used instead.
     */
    @InternalKaikenApi
    fun <InjectorType : Injector> addInjectorFactoryOverride(
        injectorHolderKClass: KClass<out InjectorHolder<*>>,
        injectorFactory: InjectorFactory<InjectorType>
    ) {
        injectorFactoryOverrides[injectorHolderKClass] = injectorFactory
    }

    /**
     * Clears all overrides previously added by [addInjectorFactoryOverride].
     */
    @InternalKaikenApi
    fun clearAllInjectorFactoryOverrides() {
        injectorFactoryOverrides.clear()
    }
}
