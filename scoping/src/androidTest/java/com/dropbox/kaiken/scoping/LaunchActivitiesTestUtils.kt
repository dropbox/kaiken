package com.dropbox.kaiken.scoping

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import test.com.dropbox.kaiken.testing.TestAuthAwareScopedActivity
import test.com.dropbox.kaiken.testing.TestAuthRequiredActivity
import test.com.dropbox.kaiken.testing.TestSimpleActivity
import test.com.dropbox.kaiken.testing.TestAuthOptionalActivity

internal fun launchAuthRequiredActivity(
    callFinishIfInvalidAuth: Boolean = true,
    includeViewingSelector: Boolean = false
): ActivityScenario<out TestAuthAwareScopedActivity> {
    TestAuthAwareScopedActivity.callFinishIfInvalidAuth = callFinishIfInvalidAuth

    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val intent = Intent(context, TestAuthRequiredActivity::class.java)

    if (includeViewingSelector) {
        intent.putViewingUserSelector(ViewingUserSelector.fromUserId("12345"))
    }

    return ActivityScenario.launch(intent)
}

internal fun launchAuthOptionalActivity(
    callFinishIfInvalidAuth: Boolean = true,
    includeViewingSelector: Boolean = false
): ActivityScenario<out TestAuthAwareScopedActivity> {
    TestAuthAwareScopedActivity.callFinishIfInvalidAuth = callFinishIfInvalidAuth

    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val intent = Intent(context, TestAuthOptionalActivity::class.java)

    if (includeViewingSelector) {
        intent.putViewingUserSelector(ViewingUserSelector.fromUserId("12345"))
    }

    return ActivityScenario.launch(intent)
}

internal fun launchSimpleActivity(): ActivityScenario<out TestSimpleActivity> {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val intent = Intent(context, TestSimpleActivity::class.java)

    return ActivityScenario.launch(intent)
}
