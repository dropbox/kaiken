package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import javax.inject.Inject

/**
 * Manages the set of authenticated users for an app.
 */
interface UserStore<T : User> {
    /**
     * Provides a flow of the [UsersEvent].  Observe this for changes related to users added, removed, and active users updated.
     *
     */
    fun getUserEvents(): Flow<UsersEvent<T>>

    suspend fun getUserById(userId: String): T?
}

@ExperimentalCoroutinesApi
@ContributesBinding(AppScope::class)
class RealUserStore<T : User> @Inject constructor(
    private val userDataSource: UserDataSource<T>,
    private val activeUserDataSource: ActiveUserDataSource,
) : UserStore<T>,
    UserDataSource<T> by userDataSource,
    ActiveUserDataSource by activeUserDataSource {

    override fun getUserEvents(): Flow<UsersEvent<T>> =
        activeUserDataSource.getActiveUser()
            .combine(getAllUsers()) { activeUserId, users ->
                Pair(activeUserId, users)
            }
            .scan(UsersEvent<T>(emptySet())) { prev, next ->
                val users = next.second
                val usersRemoved = prev.users.minusById(users)
                val usersAdded = users.minusById(prev.users)

                UsersEvent(users, usersAdded, usersRemoved, next.first)
            }
            .drop(1)

    override suspend fun getUserById(userId: String): T? {
        return getAllUsers().first().firstOrNull { it.userId == userId }
    }
}

internal fun <T : User> Set<T>.minusById(elements: Set<T>): Set<T> {
    val result = toMutableSet()
    forEach { item ->
        if (elements.any { item.userId == it.userId }) {
            result.remove(item)
        }
    }
    return result
}
