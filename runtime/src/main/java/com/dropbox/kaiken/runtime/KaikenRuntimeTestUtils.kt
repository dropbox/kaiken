package com.dropbox.kaiken.runtime

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStore
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.annotation.InternalKaikenApi
import com.dropbox.kaiken.runtime.KaikenRuntimeTestUtils.testMode
import kotlin.reflect.KClass

object KaikenRuntimeTestUtils {
    private val injectorFactoryOverrides = mutableMapOf<KClass<out InjectorHolder<*>>, InjectorFactory<*>>()
    private val injectorOverrides = mutableMapOf<KClass<out Any>, InjectorHolder<Injector>>()

    @InternalKaikenApi
    var testMode: Boolean = false

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

    internal inline fun <reified InjectorHolderType : InjectorHolder<*>> injectorHolderOverrideFor(
        fragmentKClass: KClass<out Fragment>
    ): InjectorHolderType? = injectorOverrides
        .filter { entry ->
            entry.key == fragmentKClass
        }
        .map { entry ->
            entry.value as InjectorHolderType
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
     * Overrides the resolution of the [Injector] for a Fragment.
     *
     * In production, injector resolution requires either a parent fragment or the parent activity
     * to be an [InjectorHolder]. This is so that instantiation of the [InjectorFactory] only happens
     * once per ViewModel lifecycle.
     *
     * However, in testing, it might be useful to completely short-circuit the resolution of the
     * injector.
     */
    @InternalKaikenApi
    fun <InjectorType : Injector> addInjectorOverride(
        injectableClass: KClass<out Fragment>,
        injector: InjectorType
    ) {
        injectorOverrides[injectableClass] = StaticInjectorHolder(injector)
    }

    /**
     * Clears all overrides previously added by [addInjectorFactoryOverride].
     */
    @InternalKaikenApi
    fun clearOverrides() {
        injectorFactoryOverrides.clear()
        injectorOverrides.clear()
    }
}

@OptIn(InternalKaikenApi::class)
private class StaticInjectorHolder<InjectorType : Injector>(
    private val injector: InjectorType
) : InjectorHolder<InjectorType> {

    init {
        require(testMode)
    }

    private val viewModelStore: ViewModelStore = ViewModelStore()

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }

    override fun getInjectorFactory(): InjectorFactory<InjectorType> {
        return object : InjectorFactory<InjectorType> {
            override fun createInjector(): InjectorType {
                return injector
            }
        }
    }
}
