package com.dropbox.kaiken.integration_tests

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.findInjector
import com.dropbox.kaiken.scoping.AuthAwareFragment
import com.dropbox.kaiken.scoping.DependencyProviderResolver
import dagger.Component
import javax.inject.Inject

@Injectable
class TestInjectorHolderFragmentWithDagger : AuthAwareFragment, Fragment(), InjectorHolder<TestComponent> {
    @Inject
    lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = ""
    }

    fun testInject() {
        inject()
    }

    override fun getInjectorFactory(): InjectorFactory<TestComponent> = daggerInjector()

}

@Component(dependencies = [Dependencies::class])
interface TestComponent : TestInjectorHolderFragmentWithDaggerInjector, Dependencies, TestInjectorHolderActivityInjector, TestSimpleFragmentInjector

interface Dependencies {
    val messages: String
        get() = "test"
}

fun DependencyProviderResolver.daggerInjector() =
        object : InjectorFactory<TestComponent> {
            override fun createInjector() =
                    buildPhotosInternalComponent(resolveDependencyProvider())
        }

private fun buildPhotosInternalComponent(
        dependencies: TestComponent
): TestComponent = DaggerTestComponent.builder().build()


internal fun Fragment.providePhotosPresenterDependencies():
        Dependencies = findInjector<TestComponent>()