package com.dropbox.kaiken.scoping

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import test.com.dropbox.kaiken.scoping.TestAuthAwareScopedActivity

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
