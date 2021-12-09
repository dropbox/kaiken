package test.com.dropbox.kaiken.testing

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.dropbox.kaiken.skeleton.scoping.ViewingUserSelector
import com.dropbox.kaiken.skeleton.scoping.putViewingUserSelector

fun launchAuthRequiredActivity(
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

fun launchAuthOptionalActivity(
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

fun launchSimpleActivity(): ActivityScenario<out TestSimpleActivity> {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val intent = Intent(context, TestSimpleActivity::class.java)

    return ActivityScenario.launch(intent)
}
