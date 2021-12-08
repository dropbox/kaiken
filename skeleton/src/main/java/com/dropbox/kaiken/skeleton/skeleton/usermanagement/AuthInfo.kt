package com.dropbox.kaiken.skeleton.skeleton.usermanagement


typealias UserId = String
typealias AccessToken = String

sealed class AuthInfo {
    data class LoggedOut(val userId: String) : AuthInfo()
    data class LoggedIn(val userId: String, val oauth2Token: String) : AuthInfo()
    object NoAuth : AuthInfo()
}

/**
 * A holder which gives basic user information from the [UserManager]
 */
data class User(val userId: String, val accessToken: String, val isActiveUser: Boolean)

/**
 * Potential information the [UserManager] can return.
 */
//TODO: add active user logic back in
data class UserState(
    /**
     * Current users authenticated with the app
     */
    val users: Set<User>,
    /**
     * Any users which have been added since the last state
     */
    val usersAdded: Set<User> = emptySet(),
    /**
     * Any users which have been removed since the last state
     */
    val usersRemoved: Set<User> = emptySet()
)
