package com.dropbox.kaiken.integration_tests.scoping

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import com.dropbox.kaiken.scoping.AuthRequiredActivity
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import test.com.dropbox.kaiken.testing.TestAuthAwareScopedActivity
import test.com.dropbox.kaiken.testing.launchAuthOptionalActivity
import test.com.dropbox.kaiken.testing.launchAuthRequiredActivity

class AuthAwareActivitiesTest {
    private lateinit var scenario: ActivityScenario<out TestAuthAwareScopedActivity>

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun givenAuthRequiredActivityThatDoesNotCallFinishIfInvalidAuthThenResolveDepsCrashes() {
        scenario = launchAuthRequiredActivity(callFinishIfInvalidAuth = false)

        scenario.onActivity { activity ->
            assertThat(activity.onCreateExceptionThrown).isInstanceOf(
                IllegalStateException::class.java
            )
        }
    }

    @Test
    fun givenAuthOptionalActivityThatDoesNotCallFinishIfInvalidAuthThenResolveDepsCrashes() {
        scenario = launchAuthOptionalActivity(callFinishIfInvalidAuth = false)

        scenario.onActivity { activity ->
            assertThat(activity.onCreateExceptionThrown).isInstanceOf(
                IllegalStateException::class.java
            )
        }
    }

    @Test
    fun givenAuthRequiredActivityWithNoViewingSelectorThenActivityFinishesImmediately() {
        scenario = launchAuthRequiredActivity(includeViewingSelector = false)

        assertThat(scenario.state).isEqualTo(Lifecycle.State.DESTROYED)
    }

    @Test
    @Ignore("CI Issues")
    fun givenAuthOptionalActivityWithNoViewingSelectorThenReturnsAppScopedDependencies() {
        scenario = launchAuthOptionalActivity(includeViewingSelector = false)

        assertThat(scenario.state).isEqualTo(Lifecycle.State.RESUMED)

        scenario.onActivity { activity ->
            val resolvedDependencies = activity.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm an app scoped dependency!")
        }

        UiTestUtils.rotateScreenTwice()

        scenario.onActivity { activity ->
            val resolvedDependencies = activity.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm an app scoped dependency!")
        }
    }

    @Test
    @Ignore("CI Issues")
    fun givenAuthRequiredActivityWithViewingSelectorThenReturnsUserScopedDependency() {
        scenario = launchAuthRequiredActivity(includeViewingSelector = true)

        assertThat(scenario.state).isEqualTo(Lifecycle.State.RESUMED)

        scenario.onActivity { activity ->
            val resolvedDependencies = activity.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()
            assertThat((activity as AuthRequiredActivity).requireViewingUserSelector())

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")
        }

        UiTestUtils.rotateScreenTwice()

        scenario.onActivity { activity ->
            val resolvedDependencies = activity.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")
        }
    }

    @Test
    @Ignore("CI Issues")
    fun givenAuthOptionalActivityWithViewingSelectorThenReturnsUserScopedDependency() {
        scenario = launchAuthOptionalActivity(includeViewingSelector = true)

        assertThat(scenario.state).isEqualTo(Lifecycle.State.RESUMED)

        scenario.onActivity { activity ->
            val resolvedDependencies = activity.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")
        }

        UiTestUtils.rotateScreenTwice()

        scenario.onActivity { activity ->
            val resolvedDependencies = activity.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")
        }
    }
}
