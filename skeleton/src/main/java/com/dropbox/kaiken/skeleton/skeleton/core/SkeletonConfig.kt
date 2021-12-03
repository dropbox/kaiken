package com.dropbox.kaiken.skeleton.skeleton.core

import android.content.Context
import android.content.Intent


//TODO mike backport skeleton config class change to xplat
interface SkeletonConfig{
     val scopedServicesFactory: AppSpecificScopedServicesFactory
}

/**
 * Specifies the entry intents that the AppSkeleton should redirect after auth has been validated, or after login.
 */
interface EntryIntentsProvider {
    /**
     * Returns the intent that the skeleton should when there is users logged in. There's two states where the
     * skeleton will redirect to it.
     *
     * 1) On application startup, if we determine that there's a user logged. The Skeleton will start and an activity
     * using the intent and put a [ViewingUserSelector] for the active user.
     *
     * 2) After user login. The Skeleton will start and an activity using the intent and put a [ViewingUserSelector]
     * for the recently added user.
     */
    fun getAuthenticatedEntryPointIntent(context: Context): Intent
}
