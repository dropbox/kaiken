@file:JvmName("ViewingUserSelectorUtils")

package com.dropbox.kaiken.skeleton.scoping

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val BUNDLE_KEY = "com.dropbox.kaiken.skeleton.scoping.VIEWING_USER_SELECTOR_BUNDLE_KEY"

/**
 * Opaque data structure to help identify what is the current viewing user on an activity or
 * fragment.
 */
@Parcelize
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
    return this.extras?.getViewingUserSelector()
}

fun Intent.requireViewingUserSelector(): ViewingUserSelector {
    return requireNotNull(this.extras?.requireViewingUserSelector())
}
