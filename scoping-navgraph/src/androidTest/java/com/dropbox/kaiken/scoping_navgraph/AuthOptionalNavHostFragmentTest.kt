package com.dropbox.kaiken.scoping_navgraph

import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Test
import test.com.dropbox.kaiken.testing.TestAuthAwareScopedActivity
import test.com.dropbox.kaiken.testing.launchAuthOptionalActivity

class AuthOptionalNavHostFragmentTest {
    lateinit var scenario: ActivityScenario<out TestAuthAwareScopedActivity>

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun givenAuthOptionalNavHostFragmentWhenAddedToAuthRequiredActivityThenFinishIfInvalidIsFalse() {
        scenario = launchAuthOptionalActivity(includeViewingSelector = true)

        scenario.addAuthOptionalNavHostFragment()

        scenario.onActivity { activity ->
            Truth.assertThat(activity.findTestAuthAwareFragment().finishIfInvalidAuth()).isFalse()
        }
    }

    private fun FragmentActivity.findTestAuthAwareFragment() =
        this.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as AuthOptionalNavHostFragment
}
