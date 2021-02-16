package com.dropbox.kaiken.integration_tests

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dropbox.kaiken.runtime.InjectorNotFoundException
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.RuntimeException

@RunWith(AndroidJUnit4::class)
class TestInjectableAnnotation {
//    private var injectorHolderScenario: ActivityScenario<TestInjectorHolderActivity>? = null
    private var simplerScenario: ActivityScenario<TestSimpleActivity>? = null

    @After
    fun teardown() {
//        injectorHolderScenario?.close()
        simplerScenario?.close()
    }

//    @Test
//    fun givenInjectorHolderActivityWHENInjectItTHENItWorks() {
//        injectorHolderScenario = ActivityScenario.launch(TestInjectorHolderActivity::class.java)
//
//        injectorHolderScenario!!.onActivity { activity ->
//            assertThat(activity.message).isNull()
//
//            // WHEN
//            activity.testInject()
//
//            // THEN
//            assertThat(activity.message).isEqualTo("Hello Activity!")
//        }
//    }
//
//    @Test
//    fun givenSimpleFragmentChildOfInjectorHolderActivityWHENInjectTHENItReachesUpForInjection() {
//        injectorHolderScenario = ActivityScenario.launch(TestInjectorHolderActivity::class.java)
//
//        injectorHolderScenario!!.onActivity { activity ->
//
//            // GIVEN
//            val fragment = TestSimpleFragment()
//            activity.addFragment(fragment)
//
//            assertThat(fragment.message).isNull()
//
//            // WHEN
//            fragment.testInject()
//
//            // THEN
//            assertThat(fragment.message).isEqualTo("Hello Fragment from Activity!")
//        }
//    }
//
//    @Test(expected = InjectorNotFoundException::class)
//    fun givenSimpleFragmentChildOfSimpleActivityWHENInjectTHENItCrashes() {
//        simplerScenario = ActivityScenario.launch(TestSimpleActivity::class.java)
//
//        try {
//            simplerScenario!!.onActivity { activity ->
//
//                // GIVEN
//                val fragment = TestSimpleFragment()
//                activity.addFragment(fragment)
//
//                assertThat(fragment.message).isNull()
//
//                // WHEN
//                fragment.testInject()
//
//                // THEN
//                // Crash
//            }
//        } catch (exception: RuntimeException) {
//            // The lambda wraps the InjectorNotFoundException in a RuntimeException so we need to
//            // unwrap it and rethrow it
//            throw exception.cause!!
//        }
//    }
//
//    @Test
//    fun givenInjectorHolderFragmentChildOfSimpleActivityWHENInjectItTHENItWorks() {
//        simplerScenario = ActivityScenario.launch(TestSimpleActivity::class.java)
//
//        simplerScenario!!.onActivity { activity ->
//            // GIVEN
//            val fragment = TestInjectorHolderFragment()
//            activity.addFragment(fragment)
//
//            assertThat(fragment.message).isNull()
//
//            // WHEN
//            fragment.testInject()
//
//            // THEN
//            assertThat(fragment.message).isEqualTo("Hello Fragment!")
//        }
//    }
//
//    @Test
//    fun givenInjectorHolderFragmentChildOfInjectorHolderActivityWHENInjectItTHENItDoesNotReachUp() {
//        injectorHolderScenario = ActivityScenario.launch(TestInjectorHolderActivity::class.java)
//
//        injectorHolderScenario!!.onActivity { activity ->
//            // GIVEN
//            val fragment = TestInjectorHolderFragment()
//            activity.addFragment(fragment)
//
//            assertThat(fragment.message).isNull()
//
//            // WHEN
//            fragment.testInject()
//
//            // THEN
//            assertThat(fragment.message).isNotEqualTo("Hello Fragment from Activity!")
//            assertThat(fragment.message).isEqualTo("Hello Fragment!")
//        }
//    }
//
//    @Test
//    fun givenInjectorHolderActivityWHENRotatedTHENSameInjectorIsReturned() {
//        injectorHolderScenario = ActivityScenario.launch(TestInjectorHolderActivity::class.java)
//
//        var injectorBeforeRotation: TestInjectorHolderActivityInjector? = null
//        var injectorAfterRotation: TestInjectorHolderActivityInjector?
//
//        // GIVEN
//        injectorHolderScenario!!.onActivity { activity ->
//            assertThat(activity.message).isNull()
//
//            injectorBeforeRotation = activity.locateInjector()
//
//            activity.testInject()
//            assertThat(activity.message).isNotNull()
//        }
//
//        // WHEN
//        UiTestUtils.rotateScreenTwice()
//
//        // THEN
//        injectorHolderScenario!!.onActivity { activity ->
//            assertThat(activity.message).isNull()
//
//            injectorAfterRotation = activity.locateInjector()
//
//            activity.testInject()
//            assertThat(activity.message).isNotNull()
//
//            assertThat(injectorAfterRotation).isSameInstanceAs(injectorBeforeRotation)
//        }
//    }
}

internal fun AppCompatActivity.addFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .add(fragment, "tag")
        .commit()

    supportFragmentManager.executePendingTransactions()
}
