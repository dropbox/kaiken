package test.com.dropbox.kaiken.testing

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.ScopedServicesProvider
import com.dropbox.kaiken.scoping.UserServices

abstract class TestActivity : AppCompatActivity() {
    override fun getApplicationContext(): Context {
        return TestApplicationContext(super.getApplicationContext())
    }
}

class TestApplicationContext(baseApplicationContext: Context) :
    ContextWrapper(baseApplicationContext), ScopedServicesProvider {
    override fun provideAppServices() = TestAppServices()
    override fun provideUserServicesOf(userId: String): UserServices? {
        return if (userId == "12345") {
            TestUserServices()
        } else {
            null
        }
    }
}

class TestAppServices : AppServices, MyDependencies {
    override fun getTeardownHelper() = error("Should not be called")
    override fun helloWorldGreeter() = noAuthHelloWorldSayer
}

private class TestUserServices : UserServices, MyDependencies {
    override fun getTeardownHelper() = error("Should not be called")
    override fun helloWorldGreeter() = userScopedHelloWorldSayer
}

private val noAuthHelloWorldSayer = object : HelloWorldGreeter {
    override fun sayHello() = "Hi! I'm an app scoped dependency!"
}

private val userScopedHelloWorldSayer = object : HelloWorldGreeter {
    override fun sayHello() = "Hi! I'm a user scoped dependency!"
}
