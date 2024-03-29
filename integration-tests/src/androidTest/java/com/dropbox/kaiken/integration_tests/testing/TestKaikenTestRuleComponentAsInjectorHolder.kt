package com.dropbox.kaiken.integration_tests

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dropbox.kaiken.integration_tests.runtime.addFragment
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.testing.KaikenTestRule
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestKaikenTestRuleComponentAsInjectorHolder {
    private var injectorHolderScenario: ActivityScenario<TestInjectorHolderActivity>? = null
    private var simplerScenario: ActivityScenario<TestSimpleActivity>? = null

    @get:Rule
    var kaikenTestRule: KaikenTestRule = KaikenTestRule(
        injectorFactoryOverrides = mapOf(
            TestInjectorHolderActivity::class to OverriddenInjectorFactoryWithDaggerActivity(),
            TestInjectorHolderFragmentWithDagger::class to OverriddenInjectorFactoryWithDagger()
        )
    )

    @After
    fun teardown() {
        injectorHolderScenario?.close()
        simplerScenario?.close()
    }

    @Test
    fun givenInjectorHolderActivityWHENInjectItTHENItUsesTheTestsRuleOverride() {
        injectorHolderScenario = ActivityScenario.launch(TestInjectorHolderActivity::class.java)

        injectorHolderScenario!!.onActivity { activity ->
            assertThat(activity.message).isEmpty()

            // WHEN
            activity.testInject()

            // THEN
            assertThat(activity.message).isEqualTo(
                "activity"
            )
        }
    }

    @Test
    fun givenSimpleFragmentChildOfInjectorHolderActivityWHENInjectTHENItReachesUpForAndUsesInjectorOverride() {
        injectorHolderScenario = ActivityScenario.launch(TestInjectorHolderActivity::class.java)

        injectorHolderScenario!!.onActivity { activity ->

            // GIVEN
            val fragment = TestSimpleFragment()
            activity.addFragment(fragment)

            assertThat(fragment.message).isEmpty()

            // WHEN
            fragment.testInject()

            // THEN
            assertThat(fragment.message).isEqualTo(
                "activity"
            )
        }
    }

    @Test
    fun givenInjectorHolderFragmentChildOfSimpleActivityWHENInjectItTHENItWorks() {
        simplerScenario = ActivityScenario.launch(TestSimpleActivity::class.java)

        simplerScenario!!.onActivity { activity ->
            // GIVEN
            val fragment = TestInjectorHolderFragmentWithDagger()
            activity.addFragment(fragment)

            assertThat(fragment.message).isEmpty()

            // WHEN
            fragment.testInject()

            // THEN
            assertThat(fragment.message).isEqualTo(
                "test"
            )
        }
    }

    @Test
    fun givenInjectorHolderFragmentChildOfInjectorHolderActivityWHENInjectItTHENItDoesNotReachUp() {
        injectorHolderScenario = ActivityScenario.launch(TestInjectorHolderActivity::class.java)

        injectorHolderScenario!!.onActivity { activity ->
            // GIVEN
            val fragment = TestInjectorHolderFragmentWithDagger()
            activity.addFragment(fragment)

            assertThat(fragment.message).isEmpty()

            // WHEN
            fragment.testInject()

            // THEN
            assertThat(fragment.message).isNotEqualTo(
                "activity"
            )
            assertThat(fragment.message).isEqualTo(
                "test"
            )
        }
    }

    /**
     * This is important. Even when overriding the injector for test purposes, we preserve the retention on
     * configuration change functionality.
     */
    @Test
    @Ignore("CI Issues")
    fun givenInjectorHolderActivityWHENRotatedTHENSameInjectorIsReturned() {
        injectorHolderScenario = ActivityScenario.launch(TestInjectorHolderActivity::class.java)

        var injectorBeforeRotation: TestInjectorHolderActivityInjector? = null
        var injectorAfterRotation: TestInjectorHolderActivityInjector?

        // GIVEN
        injectorHolderScenario!!.onActivity { activity ->
            assertThat(activity.message).isEmpty()

            injectorBeforeRotation = activity.locateInjector()

            activity.testInject()
            assertThat(activity.message).isNotNull()
        }

        // WHEN
        UiTestUtils.rotateScreenTwice()

        // THEN
        injectorHolderScenario!!.onActivity { activity ->
            assertThat(activity.message).isEmpty()

            injectorAfterRotation = activity.locateInjector()

            activity.testInject()
            assertThat(activity.message).isNotNull()

            assertThat(injectorAfterRotation).isSameInstanceAs(injectorBeforeRotation)
        }
    }
}

class OverriddenInjectorFactoryWithDagger : InjectorFactory<TestComponent> {
    override fun createInjector(): TestComponent {
        return DaggerTestComponent.builder().dependencies(object : Dependencies {}).build()
    }
}

class OverriddenInjectorFactoryWithDaggerActivity : InjectorFactory<TestComponent> {
    override fun createInjector(): TestComponent {
        return DaggerTestComponent.builder().dependencies(object : Dependencies {
            override val messages: String
                get() = "activity"
        }).build()
    }
}
