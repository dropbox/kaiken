package com.dropbox.kaiken.scoping

import android.app.Activity
import androidx.fragment.app.Fragment

interface UserProvider {
    val user: ViewingUserSelector?
        get() = when (this) {
            is Fragment -> arguments?.getViewingUserSelector()
            is Activity -> intent?.extras?.getViewingUserSelector()
            else -> null
        }
}

interface RequiredUserProvider : UserProvider {
    override val user: ViewingUserSelector
        get() = super.user ?: error("Must have user")
}
