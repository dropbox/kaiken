package com.dropbox.kaiken.integration_tests

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.kaiken.annotation.AutoInjectable
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.scoping.AuthRequiredActivity
import javax.inject.Inject

@AutoInjectable(dependency = LaunchDependencies::class)
class TestInjectorHolderActivity : AppCompatActivity(), AuthRequiredActivity,
        InjectorHolder<TestInjectorHolderActivityInjector> {
    @Inject
    lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        if (android.os.Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        super.onCreate(savedInstanceState)
    }

    override fun getInjectorFactory() = testInjectorHolderActivityComponentinjector()

    fun testInject() {
        inject()
    }
}

interface LaunchDependencies {
    fun getLifecycleLogger(): String
}

