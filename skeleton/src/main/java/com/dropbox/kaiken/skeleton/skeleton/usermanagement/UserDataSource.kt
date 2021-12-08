package com.dropbox.kaiken.skeleton.skeleton.usermanagement

import kotlinx.coroutines.flow.Flow

interface UserDataSource<T : User> {
    fun addUser(user: T): Boolean
    fun removeUser(userId: String): Boolean
    fun getAllUsers(): Flow<Set<T>>
    fun getUser(userId: String): Flow<T>
}
