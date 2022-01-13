package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.core.SkeletonUser

/**
 * Potential information the [UserStore] can return.
 */
data class UsersEvent(
    /**
     * Current users authenticated with the app
     */
    val users: Set<SkeletonUser>,
    /**
     * Any users which have been added since the last state
     */
    val usersAdded: Set<SkeletonUser> = emptySet(),
    /**
     * Any users which have been removed since the last state
     */
    val usersRemoved: Set<SkeletonUser> = emptySet(),
)
