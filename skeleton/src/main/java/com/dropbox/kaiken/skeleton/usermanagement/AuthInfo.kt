package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.core.SkeletonAccessToken
import com.dropbox.kaiken.skeleton.core.SkeletonUser

//interface User {
//    val userId: String
//    val accessToken: SkeletonAccessToken
//}

/**
 * Potential information the [UserStore] can return.
 */
data class UsersEvent(
    /**
     * Current users authenticated with the app
     */
    internal val users: Set<SkeletonUser>,
    /**
     * Any users which have been added since the last state
     */
    val usersAdded: Set<SkeletonUser> = emptySet(),
    /**
     * Any users which have been removed since the last state
     */
    val usersRemoved: Set<SkeletonUser> = emptySet(),
    /**
     * The UserID which is identified as active, null if none is set.
     */
    val activeUserId: String? = null
)
