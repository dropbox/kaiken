package com.dropbox.kaiken.integration_tests.scoping

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import com.dropbox.kaiken.scoping.AuthAwareScopeOwnerFragment
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector
import test.com.dropbox.kaiken.testing.TestActivity
import test.com.dropbox.kaiken.testing.TestAuthAwareFragment
import test.com.dropbox.kaiken.testing.TestAuthAwareScopedFragment
import test.com.dropbox.kaiken.testing.TestAuthOptionalFragment
import test.com.dropbox.kaiken.testing.TestAuthRequiredFragment

internal const val FRAGMENT_TAG = "AUTH_AWARE_FRAGMENT"
internal const val CHILD_FRAGMENT_TAG = "CHILD_AUTH_AWARE_FRAGMENT"

internal fun ActivityScenario<out TestActivity>.addTestAuthRequiredFragment(
    callFinishIfInvalidAuth: Boolean = true,
    includeViewingSelector: Boolean = false
) {
    TestAuthAwareScopedFragment.callFinishIfInvalidAuth = callFinishIfInvalidAuth
    addFragment(TestAuthRequiredFragment(), includeViewingSelector)
}

internal fun ActivityScenario<out TestActivity>.addTestAuthOptionalFragment(
    callFinishIfInvalidAuth: Boolean = true,
    includeViewingSelector: Boolean = false
) {
    TestAuthAwareScopedFragment.callFinishIfInvalidAuth = callFinishIfInvalidAuth
    addFragment(TestAuthOptionalFragment(), includeViewingSelector)
}

internal fun ActivityScenario<out TestActivity>.addTestAuthAwareFragment() {
    // Why false?
    // AuthAware fragment never require a viewing selector because they inherit the scope
    // from the parent activity/fragment.
    addFragment(TestAuthAwareFragment(), false)
}

internal fun <T> T.addChildTestAuthAwareFragment()
        where T : AuthAwareScopeOwnerFragment, T : Fragment {
    this.childFragmentManager
        .beginTransaction()
        .add(TestAuthAwareFragment(), CHILD_FRAGMENT_TAG)
        .commitAllowingStateLoss()
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
