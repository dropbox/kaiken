package com.dropbox.kaiken.integration_tests

import androidx.appcompat.app.AppCompatActivity
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder

@com.dropbox.kaiken.annotation.Furiex
@Injectable
class AnyFuriex : AppCompatActivity(), InjectorHolder<AnyFuriexInjector> {

    override fun getInjectorFactory() = fakeInjectorFactory()

    private fun fakeInjectorFactory() = object : InjectorFactory<AnyFuriexInjector> {
        override fun createInjector(): AnyFuriexInjector {
            return AnyFuriexInjector { }
        }
    }
}
