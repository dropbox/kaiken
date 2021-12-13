package com.dropbox.kaiken.skeleton.usermanagement

import kotlinx.coroutines.flow.Flow

interface UserDataSource<T : User> {
    suspend fun add(user: T): Boolean
    suspend fun remove(userId: String): Boolean
    fun getAllUsers(): Flow<Set<T>>
}