package com.dropbox.kaiken.integration_tests

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.common.inject.AuthRequiredScope
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.scoping.AuthRequiredActivity
import javax.inject.Inject

@Injectable(scope = [AuthRequiredScope::class])
class TestInjectorHolderActivity :
    AppCompatActivity(),
    AuthRequiredActivity,
    InjectorHolder<TestInjectorHolderActivityInjector> {
    @Inject
    lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        message = ""
        if (android.os.Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        super.onCreate(savedInstanceState)
    }

    override fun getInjectorFactory() = fakeInjectorFactory()

    fun testInject() {
        inject()
    }
}

private fun fakeInjectorFactory() = object : InjectorFactory<TestInjectorHolderActivityInjector> {
    override fun createInjector() = FakeInjector()
}

class FakeInjector : TestInjectorHolderActivityInjector, TestSimpleFragmentInjector {
    override fun inject(activity: TestInjectorHolderActivity) {
        activity.message = "Hello Activity!"
    }

    override fun inject(fragment: TestSimpleFragment) {
        fragment.message = "Hello Fragment from Activity!"
    }
}
