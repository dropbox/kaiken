package com.dropbox.kaiken.scoping

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import test.com.dropbox.kaiken.testing.TestAuthAwareFragment
import test.com.dropbox.kaiken.testing.TestAuthAwareScopedFragment
import test.com.dropbox.kaiken.testing.TestSimpleActivity
import test.com.dropbox.kaiken.testing.launchSimpleActivity

class AuthScopeOwnersFragmentsTest {
    private lateinit var scenario: ActivityScenario<out TestSimpleActivity>

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun givenAuthRequiredFragmentThatDoesNotCallFinishIfInvalidAuthThenResolveDepsCrashes() {
        scenario = launchSimpleActivity()

        scenario.addTestAuthRequiredFragment(callFinishIfInvalidAuth = false)

        scenario.onActivity { activity ->
            val testObject = activity.findTestAuthAwareScopedFragment()

            assertThat(testObject.onAttachExceptionThrown).isInstanceOf(
                IllegalStateException::class.java
            )
        }
    }

    @Test
    fun givenAuthOptionalFragmentThatDoesNotCallFinishIfInvalidAuthThenResolveDepsCrashes() {
        scenario = launchSimpleActivity()

        scenario.addTestAuthOptionalFragment(callFinishIfInvalidAuth = false)

        scenario.onActivity { activity ->
            val testObject = activity.findTestAuthAwareScopedFragment()

            assertThat(testObject.onAttachExceptionThrown).isInstanceOf(
                IllegalStateException::class.java
            )
        }
    }

    @Test
    @Ignore("Flake")
    fun givenAuthRequiredFragmentWithNoViewingSelectorThenActivityFinishesImmediately() {
        scenario = launchSimpleActivity()

        assertThat(scenario.state).isEqualTo(Lifecycle.State.RESUMED)

        scenario.addTestAuthRequiredFragment()

        // Why isLessThan(RESUMED) and not isEqualTo(DESTROYED)?
        // There's some race condition when sometimes it will make it to DESTROYED, but sometimes
        // it only will make it back to STARTED.
        assertThat(scenario.state).isLessThan(Lifecycle.State.RESUMED)
    }

    @Test
    fun givenAuthOptionalFragmentWithNoViewingSelectorThenReturnsAppScopedDependencies() {
        scenario = launchSimpleActivity()

        scenario.addTestAuthOptionalFragment()

        assertThat(scenario.state).isEqualTo(Lifecycle.State.RESUMED)

        scenario.assertFragmentCanResolveAppScopedDependency()

        UiTestUtils.rotateScreenTwice()

        scenario.assertFragmentCanResolveAppScopedDependency()
    }

    @Test
    fun givenAuthRequiredFragmentWithViewingSelectorThenReturnsUserScopedDependency() {
        scenario = launchSimpleActivity()

        scenario.addTestAuthRequiredFragment(includeViewingSelector = true)

        assertThat(scenario.state).isEqualTo(Lifecycle.State.RESUMED)

        scenario.assertFragmentCanResolveUserScopedDependency()

        UiTestUtils.rotateScreenTwice()

        scenario.assertFragmentCanResolveUserScopedDependency()
    }

    @Test
    fun givenAuthOptionalFragmentWithViewingSelectorThenReturnsUserScopedDependency() {
        scenario = launchSimpleActivity()

        scenario.addTestAuthOptionalFragment(includeViewingSelector = true)

        assertThat(scenario.state).isEqualTo(Lifecycle.State.RESUMED)

        scenario.assertFragmentCanResolveUserScopedDependency()

        UiTestUtils.rotateScreenTwice()

        scenario.assertFragmentCanResolveUserScopedDependency()
    }

    @Test
    fun givenFragmentInsideAuthScopeOwnerFragmentWithViewingSelectorThenScopeIsInherited() {
        scenario = launchSimpleActivity()

        // Parent (user scope owner)
        scenario.addTestAuthOptionalFragment(includeViewingSelector = true)

        // Child (auth aware fragment that inherits scope)
        scenario.onActivity { activity ->
            val parentFragment = activity.findTestAuthAwareScopedFragment()
            parentFragment.addChildTestAuthAwareFragment()
        }

        scenario.onActivity { activity ->
            val parentFragment = activity.findTestAuthAwareScopedFragment()
            val testObject = parentFragment.findChildTestAuthAwareFragment()

            val resolvedDependencies = testObject.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")
        }

        assertThat(scenario.state).isEqualTo(Lifecycle.State.RESUMED)
    }

    private fun FragmentActivity.findTestAuthAwareScopedFragment() =
        this.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as TestAuthAwareScopedFragment

    private fun Fragment.findChildTestAuthAwareFragment() =
        this.childFragmentManager.findFragmentByTag(CHILD_FRAGMENT_TAG) as TestAuthAwareFragment

    private fun ActivityScenario<out TestSimpleActivity>
    .assertFragmentCanResolveAppScopedDependency() {
        onActivity { activity ->
            val testObject = activity.findTestAuthAwareScopedFragment()
            assertThat(testObject.onAttachExceptionThrown).isNull()

            val resolvedDependencies = testObject.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm an app scoped dependency!")
        }
    }

    private fun ActivityScenario<out TestSimpleActivity>
    .assertFragmentCanResolveUserScopedDependency() {
        onActivity { activity ->
            val testObject = activity.findTestAuthAwareScopedFragment()
            assertThat(testObject.onAttachExceptionThrown).isNull()

            val resolvedDependencies = testObject.resolvedDependencies
            assertThat(resolvedDependencies).isNotNull()

            val helloWorldSayer = resolvedDependencies!!.helloWorldGreeter()
            assertThat(helloWorldSayer.sayHello()).isEqualTo("Hi! I'm a user scoped dependency!")
        }
    }
}
