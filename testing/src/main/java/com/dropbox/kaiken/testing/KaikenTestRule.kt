package com.dropbox.kaiken.testing

import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.InternalKaikenApi
import com.dropbox.kaiken.runtime.Kaiken
import org.junit.rules.ExternalResource
import kotlin.reflect.KClass

class KaikenTestRule() : ExternalResource() {
    constructor(injectorHolderKClass: KClass<out InjectorHolder<*>>, injectorFactory: InjectorFactory<*>) : this() {
        this.injectorHolderKClass = injectorHolderKClass
        this.injectorFactory = injectorFactory
    }

    private lateinit var injectorHolderKClass: KClass<out InjectorHolder<*>>
    private lateinit var injectorFactory: InjectorFactory<*>

    @OptIn(InternalKaikenApi::class)
    fun setInjectorFactoryHolderOverrideFor(
        injectorHolderKClass: KClass<out InjectorHolder<*>>,
        injectorFactory: InjectorFactory<*>
    ) {
        Kaiken.addInjectorFactoryOverride(injectorHolderKClass, injectorFactory)
    }

    @OptIn(InternalKaikenApi::class)
    override fun before() {
        Kaiken.clearAllInjectorFactoryOverrides()
        if (::injectorHolderKClass.isInitialized && ::injectorFactory.isInitialized) {
            Kaiken.addInjectorFactoryOverride(injectorHolderKClass, injectorFactory)
        }
    }

    @OptIn(InternalKaikenApi::class)
    override fun after() {
        Kaiken.clearAllInjectorFactoryOverrides()
    }
}
