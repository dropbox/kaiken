package com.dropbox.kaiken.skeleton.skeleton.usermanagement.auth

/**
 * A holder class to feed the basic user information
 */
data class UserInput(val userId: String, val username: String, val accessToken: String = "default")
