package com.dropbox.kaiken.integration

import androidx.appcompat.app.AppCompatActivity
import com.dropbox.kaiken.annotation.AutoInjectable
import javax.inject.Singleton

@Singleton
class TestInjectorHolderActivity : AppCompatActivity() {//, AuthRequiredActivity,
//        InjectorHolder<TestInjectorHolderActivityInjector> {
//    @Inject
//    lateinit var message: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        if (android.os.Build.VERSION.SDK_INT != 26) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//        }
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun getInjectorFactory() = testInjectorHolderActivityComponentinjector()
}

//interface LaunchDependencies {
//    fun getLifecycleLogger(): String
//}




