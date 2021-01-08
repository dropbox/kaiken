package com.dropbox.kaiken.scoping

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import test.com.dropbox.kaiken.testing.MyDependencies
import test.com.dropbox.kaiken.testing.TestApplicationContext
import test.com.dropbox.kaiken.testing.TestAuthOptionalBroadcastReceiver
import test.com.dropbox.kaiken.testing.TestAuthRequiredBroadcastReceiver

class AuthAwareBroadcastReceiverTest {
    @Test
    fun givenAuthRequiredReceiverWithViewingSelectorWhenOnReceiveThenReturnsUserScopedDep() {
        val testObject = TestAuthRequiredBroadcastReceiver()

        val context = TestContext(
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        val intent = Intent().apply {
            putViewingUserSelector(ViewingUserSelector.fromUserId("12345"))
        }

        val resolvedDependencies: MyDependencies? = testObject.onReceiveResolveDependencyProvider(
            context, intent
        )

        assertThat(resolvedDependencies).isNotNull()

        val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
        assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")
    }

    @Test
    fun givenAuthRequiredReceiverWithNoViewingSelectorWhenOnReceiveThenReturnsNullDeps() {
        val testObject = TestAuthRequiredBroadcastReceiver()

        val context = TestContext(
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        val intent = Intent()

        val resolvedDependencies: MyDependencies? = testObject.onReceiveResolveDependencyProvider(
            context, intent
        )

        assertThat(resolvedDependencies).isNull()
    }

    @Test
    fun givenAuthOptionalReceiverWithViewingSelectorWhenOnReceiveThenReturnsUserScopedDep() {
        val testObject = TestAuthOptionalBroadcastReceiver()

        val context = TestContext(
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        val intent = Intent().apply {
            putViewingUserSelector(ViewingUserSelector.fromUserId("12345"))
        }

        val resolvedDependencies: MyDependencies? = testObject.onReceiveResolveDependencyProvider(
            context, intent
        )

        assertThat(resolvedDependencies).isNotNull()

        val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
        assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")
    }

    @Test
    fun givenAuthOptionalReceiverWithNoViewingSelectorWhenOnReceiveThenReturnsAppScopedDeps() {
        val testObject = TestAuthOptionalBroadcastReceiver()

        val context = TestContext(
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        val intent = Intent()

        val resolvedDependencies: MyDependencies? = testObject.onReceiveResolveDependencyProvider(
            context, intent
        )

        assertThat(resolvedDependencies).isNotNull()

        val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
        assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm an app scoped dependency!")
    }
}

private class TestContext(baseContext: Context) : ContextWrapper(baseContext) {
    override fun getApplicationContext(): Context {
        return TestApplicationContext(super.getApplicationContext())
    }
}
