package com.dropbox.kaiken.scoping.internal

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.ScopedServicesProvider
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.google.common.truth.Truth.assertThat
import kotlin.test.fail
import org.junit.Test

class ActivityAuthHelperTest {
    private val fakeScopedServicesProvider = FakeScopeServicesProvider()

    @Test(expected = IllegalStateException::class)
    fun `GIVEN that validateAuth HAS NOT been called WHEN resolveDependencyProvider THEN throws`() {
        // GIVEN
        val viewingUserSelector: ViewingUserSelector? = null
        val authRequired = false

        val testObject = AuthHelper(
            fakeScopedServicesProvider,
            viewingUserSelector,
            authRequired
        )

        // WHEN
        testObject.resolveDependencyProvider<Any?>()

        // THEN
        // throws
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN validateAuth returned false WHEN resolveDependencyProvider THEN throws`() {
        // GIVEN
        val viewingUserSelector: ViewingUserSelector? = null
        val authRequired = true

        val testObject = AuthHelper(
            fakeScopedServicesProvider,
            viewingUserSelector,
            authRequired
        )

        val validAuth = testObject.validateAuth()
        assertThat(validAuth).isFalse()

        // WHEN
        testObject.resolveDependencyProvider<Any?>()

        // THEN
        // throws
    }

    @Test
    fun `GIVEN that viewingUserSelector is null and NOT authRequired WHEN resolveDependencyProvider THEN returns no auth deps`() {
        // GIVEN
        val viewingUserSelector: ViewingUserSelector? = null
        val authRequired = false

        val testObject = AuthHelper(
            fakeScopedServicesProvider,
            viewingUserSelector,
            authRequired
        )

        // WHEN
        val validAuth = testObject.validateAuth()
        val resolvedProvider = testObject.resolveDependencyProvider<MyDependencyProvider>()

        // THEN
        assertThat(validAuth).isTrue()
        assertThat(resolvedProvider.helloWorldSayer()).isEqualTo(noAuthHelloWorldSayer)
    }

    @Test
    fun `GIVEN that viewingUserSelector is null and authRequired WHEN validateAuth THEN returns false`() {
        // GIVEN
        val viewingUserSelector: ViewingUserSelector? = null
        val authRequired = true

        val testObject = AuthHelper(
            fakeScopedServicesProvider,
            viewingUserSelector,
            authRequired
        )

        // WHEN
        val validAuth = testObject.validateAuth()

        // THEN
        assertThat(validAuth).isFalse()
    }

    @Test
    fun `GIVEN that viewingUserSelector is NOT NULL and user not available WHEN validateAuth THEN false`() {
        // GIVEN
        val viewingUserSelector = ViewingUserSelector.fromUserId("notExistingUserId")
        val authRequired = false

        val testObject = AuthHelper(
            fakeScopedServicesProvider,
            viewingUserSelector,
            authRequired
        )

        // WHEN
        val validAuth = testObject.validateAuth()

        // THEN
        assertThat(validAuth).isFalse()
    }

    @Test
    fun `GIVEN that viewingUserSelector is NOT NULL and user available WHEN resolveDependencyProvider THEN returns auth deps`() {
        // GIVEN
        val viewingUserSelector = ViewingUserSelector.fromUserId("validUserId")
        val authRequired = false

        val testObject = AuthHelper(
            fakeScopedServicesProvider,
            viewingUserSelector,
            authRequired
        )

        // WHEN
        val validAuth = testObject.validateAuth()
        val resolvedProvider = testObject.resolveDependencyProvider<MyDependencyProvider>()

        // THEN
        assertThat(validAuth).isTrue()
        assertThat(resolvedProvider.helloWorldSayer()).isEqualTo(userScopedHelloWorldSayer)
    }
}

private class FakeScopeServicesProvider : ScopedServicesProvider {
    var provideAppServicesResult = FakeAppServices()
    var provideUserServicesOfResult: UserServices = FakeUserServices()

    override fun provideAppServices() = provideAppServicesResult

    override fun provideUserServicesOf(userId: String): UserServices? {
        return if (userId == "validUserId") {
            provideUserServicesOfResult
        } else {
            null
        }
    }
}

private class FakeAppServices : AppServices, MyDependencyProvider {
    override fun getTeardownHelper() = fail("Should not have been called")

    override fun helloWorldSayer() = noAuthHelloWorldSayer
}

private class FakeUserServices : UserServices, MyDependencyProvider {
    override fun getTeardownHelper() = fail("Should not have been called")

    override fun helloWorldSayer() = userScopedHelloWorldSayer
}

private interface MyDependencyProvider {
    fun helloWorldSayer(): HelloWorldSayer
}

private interface HelloWorldSayer {
    fun sayHello(): String
}

private val noAuthHelloWorldSayer = object : HelloWorldSayer {
    override fun sayHello() = "Hi! I'm a no-auth hello world sayer!"
}

private val userScopedHelloWorldSayer = object : HelloWorldSayer {
    override fun sayHello() = "Hi! I'm a user scoped hello world sayer!"
}
