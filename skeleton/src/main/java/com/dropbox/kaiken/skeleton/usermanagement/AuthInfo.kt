package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.core.SkeletonAccessToken

interface User {
    val userId: String
    val accessToken: SkeletonAccessToken
}

/**
 * Potential information the [UserStore] can return.
 */
// TODO: add active user logic back in ???
data class UsersEvent<T : User>(
    /**
     * Current users authenticated with the app
     */
    internal val users: Set<T>,
    /**
     * Any users which have been added since the last state
     */
    val usersAdded: Set<T> = emptySet(),
    /**
     * Any users which have been removed since the last state
     */
    val usersRemoved: Set<T> = emptySet(),
    /**
     * The UserID which is identified as active, null if none is set.
     */
    val activeUserId: String? = null
)
