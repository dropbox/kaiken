package com.dropbox.kaiken.integration_tests.testing

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dropbox.kaiken.integration_tests.TestAuthAwareFragment
import com.dropbox.kaiken.integration_tests.TestAuthAwareFragmentInjector
import com.dropbox.kaiken.integration_tests.TestSimpleActivity
import com.dropbox.kaiken.integration_tests.runtime.addFragment
import com.dropbox.kaiken.testing.KaikenTestRule
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestKaikenTestRule {
    private var simplerScenario: ActivityScenario<TestSimpleActivity>? = null

    @get:Rule
    var kaikenTestRule: KaikenTestRule = KaikenTestRule(
        injectorOverride = TestAuthAwareFragment::class to FakeInjector()
    )

    @After
    fun teardown() {
        simplerScenario?.close()
    }

    @Test
    fun givenAuthAwareFragmentChildOfSimpleActivityWHENInjectItTHENItWorks() {
        simplerScenario = ActivityScenario.launch(TestSimpleActivity::class.java)

        simplerScenario!!.onActivity { activity ->
            // GIVEN
            val fragment = TestAuthAwareFragment()
            activity.addFragment(fragment)

            assertThat(fragment.message).isEmpty()

            // WHEN
            fragment.testInject()

            // THEN
            assertThat(fragment.message).isEqualTo(
                "Hello Fragment! I've overridden the default injector! Muaha Muaha!"
            )
        }
    }
}

class FakeInjector : TestAuthAwareFragmentInjector {
    override fun inject(fragment: TestAuthAwareFragment) {
        fragment.message = "Hello Fragment! I've overridden the default injector! Muaha Muaha!"
    }
}
