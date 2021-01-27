package com.dropbox.kaiken.scoping_navgraph

import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Test
import test.com.dropbox.kaiken.testing.TestAuthAwareScopedActivity
import test.com.dropbox.kaiken.testing.launchAuthRequiredActivity

class AuthRequiredNavHostFragmentTest {
    lateinit var scenario: ActivityScenario<out TestAuthAwareScopedActivity>

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun givenAuthRequiredNavHostFragmentWhenAddedToAuthRequiredActivityThenFinishIfInvalidIsTrue() {
        scenario = launchAuthRequiredActivity(includeViewingSelector = true)

        scenario.addAuthRequiredNavHostFragment()

        scenario.onActivity {
            Truth.assertThat(it.findTestAuthAwareFragment().finishIfInvalidAuth()).isTrue()
        }
    }

    private fun FragmentActivity.findTestAuthAwareFragment() =
        this.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as AuthRequiredNavHostFragment
}
