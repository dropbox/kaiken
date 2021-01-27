package com.dropbox.kaiken.scoping_navgraph

import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import test.com.dropbox.kaiken.testing.TestAuthAwareScopedActivity
import test.com.dropbox.kaiken.testing.launchAuthRequiredActivity

class AuthAwareNavHostFragmentTest {
    lateinit var scenario: ActivityScenario<out TestAuthAwareScopedActivity>

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun givenAuthAwareNavHostFragmentWhenAddedToAuthRequiredActivityThenFinishIfInvalidIsFalse() {
        scenario = launchAuthRequiredActivity(includeViewingSelector = true)

        scenario.addAuthAwareNavHostFragment()

        scenario.onActivity {
            assertThat(it.findTestAuthAwareFragment().finishIfInvalidAuth()).isFalse()
        }
    }

    private fun FragmentActivity.findTestAuthAwareFragment() =
        this.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as AuthAwareNavHostFragment
}
