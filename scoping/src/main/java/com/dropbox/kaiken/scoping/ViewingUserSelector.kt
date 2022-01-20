@file:JvmName("ViewingUserSelectorUtils")

package com.dropbox.kaiken.scoping

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

private const val BUNDLE_KEY = "com.dropbox.kaiken.skeleton.scoping.VIEWING_USER_SELECTOR_BUNDLE_KEY"

private const val DEEP_LINK_EXTRAS = "android-support-nav:controller:deepLinkExtras"

/**
 * Opaque data structure to help identify what is the current viewing user on an activity or
 * fragment.
 */
@Parcelize
@SuppressWarnings("ParcelCreator") // https://youtrack.jetbrains.com/issue/KT-19300
data class ViewingUserSelector(internal val userId: String) : Parcelable {
    companion object {
        @JvmStatic
        fun fromUserId(userId: String): ViewingUserSelector {
            return ViewingUserSelector(userId)
        }
    }
}

fun Bundle.putViewingUserSelector(userSelector: ViewingUserSelector) {
    this.putParcelable(BUNDLE_KEY, userSelector)
}

fun Bundle.hasViewingUserSelector() = this.containsKey(BUNDLE_KEY)

fun Bundle.getViewingUserSelector(): ViewingUserSelector? {
    return this.get(BUNDLE_KEY) as ViewingUserSelector?
}

fun Bundle.requireViewingUserSelector(): ViewingUserSelector {
    return requireNotNull(getViewingUserSelector())
}

fun Intent.putViewingUserSelector(userSelector: ViewingUserSelector) {
    this.putExtra(BUNDLE_KEY, userSelector)
}

fun Intent.hasViewingUserSelector() = this.hasExtra(BUNDLE_KEY)

fun Intent.getViewingUserSelector(): ViewingUserSelector? {
    return extras?.getViewingUserSelector() ?: return getDeepLinkViewingUserSelector()
}

private fun Intent.getDeepLinkViewingUserSelector(): ViewingUserSelector? {
    val deepLinkBundle = this.extras?.get(DEEP_LINK_EXTRAS) as Bundle?
    return deepLinkBundle?.getViewingUserSelector()
}

fun Intent.requireViewingUserSelector(): ViewingUserSelector {
    return requireNotNull(this.extras?.requireViewingUserSelector())
}
