package com.dropbox.kaiken.integration_tests

import androidx.fragment.app.Fragment
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder

@com.dropbox.kaiken.annotation.Injectable
open class AnyFuriex : Fragment(), InjectorHolder<AnyFuriexInjector> {

    override fun getInjectorFactory() = fakeInjectorFactory()

    private fun fakeInjectorFactory() = object : InjectorFactory<AnyFuriexInjector> {
        override fun createInjector(): AnyFuriexInjector {
            return object : AnyFuriexInjector {
                override fun inject(activity: AnyFuriex) {
                }
            }
        }
    }
}
