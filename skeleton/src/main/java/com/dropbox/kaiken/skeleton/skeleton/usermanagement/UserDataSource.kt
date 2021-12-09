package com.dropbox.kaiken.skeleton.skeleton.usermanagement

import kotlinx.coroutines.flow.Flow

interface UserDataSource<T : User> {
    suspend fun add(user: User): Boolean
    suspend fun remove(userId: String): Boolean
    fun getAllUsers(): Flow<Set<T>>
}

interface DataSource<T> {
    fun add(t: T)
    fun remove(t: T)
}
