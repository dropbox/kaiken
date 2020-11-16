package com.dropbox.kaiken.scoping

import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import test.com.dropbox.kaiken.scoping.TestAuthAwareFragment
import test.com.dropbox.kaiken.scoping.TestAuthAwareScopedActivity

class AuthAwareFragmentTest {
    lateinit var scenario: ActivityScenario<out TestAuthAwareScopedActivity>

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun givenFragmentInAuthOptionalActivityWithNoViewingSelectorThenReturnsAppScopedDependencies() {
        scenario = launchAuthOptionalActivity(includeViewingSelector = false)

        scenario.addTestAuthAwareFragment()

        scenario.assertFragmentCanResolveAppScopedDependency()

        UiTestUtils.rotateScreenTwice()

        scenario.assertFragmentCanResolveAppScopedDependency()
    }

    @Test
    fun givenFragmentInAuthRequiredActivityWithViewingSelectorThenReturnsUserScopedDependency() {
        scenario = launchAuthRequiredActivity(includeViewingSelector = true)

        scenario.addTestAuthAwareFragment()

        scenario.assertFragmentCanResolveUserScopedDependency()

        UiTestUtils.rotateScreenTwice()

        scenario.assertFragmentCanResolveUserScopedDependency()
    }

    @Test
    fun givenFragmentInAuthOptionalActivityWithViewingSelectorThenReturnsUserScopedDependency() {
        scenario = launchAuthOptionalActivity(includeViewingSelector = true)

        scenario.addTestAuthAwareFragment()

        scenario.assertFragmentCanResolveUserScopedDependency()

        UiTestUtils.rotateScreenTwice()

        scenario.assertFragmentCanResolveUserScopedDependency()
    }

    private fun FragmentActivity.findTestAuthAwareFragment() =
        this.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as TestAuthAwareFragment

    private fun ActivityScenario<out TestAuthAwareScopedActivity>
    .assertFragmentCanResolveAppScopedDependency() {
        onActivity { activity ->
            assertThat(activity.onCreateExceptionThrown).isNull()
            val testObject = activity.findTestAuthAwareFragment()

            val resolvedDependencies = testObject.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm an app scoped dependency!")

            assertThat(testObject.getViewingUserSelector())
                .isEqualTo(activity.getViewingUserSelector())
        }
    }

    private fun ActivityScenario<out TestAuthAwareScopedActivity>
    .assertFragmentCanResolveUserScopedDependency() {
        onActivity { activity ->
            assertThat(activity.onCreateExceptionThrown).isNull()
            val testObject = activity.findTestAuthAwareFragment()

            val resolvedDependencies = testObject.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")

            assertThat(testObject.getViewingUserSelector())
                .isEqualTo(activity.getViewingUserSelector())
        }
    }
}
