package com.dropbox.kaiken.scoping_navgraph

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector
import test.com.dropbox.kaiken.testing.TestActivity

internal const val FRAGMENT_TAG = "AUTH_AWARE_FRAGMENT"

internal fun ActivityScenario<out TestActivity>.addAuthAwareNavHostFragment(
    includeViewingSelector: Boolean = false
) {
    addFragment(AuthAwareNavHostFragment(), includeViewingSelector)
}

internal fun ActivityScenario<out TestActivity>.addAuthRequiredNavHostFragment(
    includeViewingSelector: Boolean = false
) {
    addFragment(AuthRequiredNavHostFragment(), includeViewingSelector)
}

internal fun ActivityScenario<out TestActivity>.addAuthOptionalNavHostFragment(
    includeViewingSelector: Boolean = false
) {
    addFragment(AuthOptionalNavHostFragment(), includeViewingSelector)
}

private fun ActivityScenario<out TestActivity>.addFragment(
    fragment: Fragment,
    includeViewingSelector: Boolean = false
) {
    if (includeViewingSelector) {
        fragment.arguments = Bundle().apply {
            putViewingUserSelector(ViewingUserSelector.fromUserId("12345"))
        }
    }

    onActivity { activity ->
        activity.supportFragmentManager
            .beginTransaction()
            .add(fragment, FRAGMENT_TAG)
            .commitAllowingStateLoss()
    }
}