package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import javax.inject.Inject

/**
 * Manages the set of authenticated users for an app.
 */
interface UserStore {
    /**
     * Provides a flow of the [UsersEvent].  Observe this for changes related to users added and removed.
     *
     */
    fun getUserEvents(): Flow<UsersEvent>

    /**
     * Provides an individual user based on the ID
     */
    suspend fun getUserById(userId: String): SkeletonUser?
}

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealUserStore @Inject constructor(
    @JvmSuppressWildcards private val users: Flow<@JvmSuppressWildcards Set<SkeletonUser>>,
) : UserStore {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserEvents(): Flow<UsersEvent> =
        users
            .scan(UsersEvent(emptySet())) { prev, next ->
                val usersRemoved = prev.users.minusById(next)
                val usersAdded = next.minusById(prev.users)

                UsersEvent(next, usersAdded, usersRemoved)
            }
            .drop(1)

    override suspend fun getUserById(userId: String): SkeletonUser? {
        return users.first().firstOrNull { it.userId == userId }
    }
}

internal fun Set<SkeletonUser>.minusById(elements: Set<SkeletonUser>): Set<SkeletonUser> {
    val result = toMutableSet()
    forEach { item ->
        if (elements.any { item.userId == it.userId }) {
            result.remove(item)
        }
    }
    return result
}

interface SkeletonMapper<T> {
    fun toSkeletonUser(from: T): SkeletonUser
}
