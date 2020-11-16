package com.dropbox.kaiken.testing

import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.InternalKaikenApi
import com.dropbox.kaiken.runtime.Kaiken
import kotlin.reflect.KClass
import org.junit.rules.ExternalResource

class KaikenTestRule : ExternalResource() {
    @OptIn(InternalKaikenApi::class)
    fun setInjectorFactoryHolderOverrideFor(
        injectorHolderKClass: KClass<out InjectorHolder<*>>,
        injectorFactory: InjectorFactory<*>
    ) {
        Kaiken.addInjectorFactoryOverride(injectorHolderKClass, injectorFactory)
    }

    @OptIn(InternalKaikenApi::class)
    override fun before() {
        super.after()
        Kaiken.clearAllInjectorFactoryOverrides()
    }

    @OptIn(InternalKaikenApi::class)
    override fun after() {
        super.after()
        Kaiken.clearAllInjectorFactoryOverrides()
    }
}
