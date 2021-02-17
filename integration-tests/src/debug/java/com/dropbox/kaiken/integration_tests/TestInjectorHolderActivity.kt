package com.dropbox.kaiken.integration_tests

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.kaiken.annotation.AutoInjectable
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.scoping.AuthRequiredActivity
import com.dropbox.kaiken.scoping.DependencyProviderResolver
import dagger.Component
import javax.inject.Inject
import javax.inject.Scope


@Scope
annotation class TestInjectorHolderActivityScope

class ATestInjectorHolderActivityScope

@TestInjectorHolderActivityScope
@Component(dependencies = [LaunchDependencies::class])
interface TestInjectorHolderActivityComponent {
    @Component.Factory
    interface Factory {
        fun create(LaunchDependencies: LaunchDependencies): TestInjectorHolderActivityComponent
    }
}

fun DependencyProviderResolver.injector(): InjectorFactory<TestInjectorHolderActivityInjector> =
        object : InjectorFactory<TestInjectorHolderActivityInjector> {
            override fun createInjector() =
                    DaggerTestInjectorHolderActivityComponent.factory().create(resolveDependencyProvider()) as
                            TestInjectorHolderActivityInjector
        }

@Injectable(COMPONENT = TestInjectorHolderActivityComponent::class)
class TestInjectorHolderActivity : AppCompatActivity(), AuthRequiredActivity,
        InjectorHolder<TestInjectorHolderActivityInjector> {
    @Inject
     lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        if (android.os.Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun getInjectorFactory() = injector()

}

interface LaunchDependencies {
    fun getLifecycleLogger(): String
}

//@AutoInjectable(dependency = LaunchDependencies::class)
//class LaunchActivity : AppCompatActivity(), AuthRequiredActivity,
//        InjectorHolder<LaunchActivityInjector> {
//    @Inject
//    lateinit var lifecycleLogger: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        inject()
//    }
//
//    override fun getInjectorFactory() = injector()
//}


//fun fakeActivityInjectorFactory() = object : InjectorFactory<TestInjectorHolderActivityInjector> {
//    override fun createInjector() = FakeActivityInjector()
//}
//
//class FakeActivityInjector :  TestInjectorHolderActivityInjector {
//    override fun inject(activity: TestInjectorHolderActivity) {
////        activity.message = "Hello Activity!"
//    }
//}