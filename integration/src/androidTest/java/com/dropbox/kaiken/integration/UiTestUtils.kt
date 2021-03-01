package com.dropbox.kaiken.integration

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Looper
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.google.common.collect.Iterables
import org.hamcrest.Matcher

object UiTestUtils {
    /**
     * A test util that rotates the screen to landscape and back to portrait. This allows for
     * testing configuration change in the ui tests.
     */
    @JvmStatic
    fun rotateScreenTwice() {
        rotateScreenTo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        sleep(1000)
        rotateScreenTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    private fun rotateScreenTo(orientation: Int) {
        waitUntilIdle()
        currentActivity!!.requestedOrientation = orientation
        waitUntilIdle()
    }

    /**
     * Waits until the main thread is idle.
     */
    @JvmStatic
    private fun waitUntilIdle() {
        assertNotMainThread()
        Espresso.onView(ViewMatchers.isRoot())
            .perform(
                object : ViewAction {
                    override fun getConstraints(): Matcher<View> {
                        return ViewMatchers.isRoot()
                    }

                    override fun getDescription(): String {
                        return "waiting until idle..."
                    }

                    override fun perform(uiController: UiController, view: View) {}
                })
    }

    /**
     * Returns the current activity. Waits until the UI thread is idle before performing the
     * check.
     */
    @JvmStatic
    private val currentActivity: Activity?
        get() {
            waitUntilIdle()
            val activity = arrayOfNulls<Activity>(1)
            InstrumentationRegistry.getInstrumentation()
                .runOnMainSync {
                    val activities = ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.RESUMED)
                    activity[0] = Iterables.getOnlyElement(activities)
                }
            return activity[0]
        }

    /**
     * Throws an exception if called from the main thread.
     */
    private fun assertNotMainThread() {
        check(!isCurrentMainThread()) { "Shouldn't be running on main thread" }
    }

    private fun isCurrentMainThread() = (Looper.getMainLooper() == Looper.myLooper())

    @JvmStatic
    private fun sleep(sleepTimeMillis: Long) {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(
                object : ViewAction {
                    override fun getConstraints(): Matcher<View> {
                        return ViewMatchers.isRoot()
                    }

                    override fun getDescription(): String {
                        return "Sleep main thread for " + sleepTimeMillis + "ms"
                    }

                    override fun perform(uiController: UiController, view: View) {
                        uiController.loopMainThreadForAtLeast(sleepTimeMillis)
                    }
                })
    }
}
