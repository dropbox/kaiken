package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.usermanagement.auth.ActiveUserManager
import com.dropbox.kaiken.skeleton.usermanagement.auth.SkeletonAuthInteractor
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Manages the set of authenticated users for an app.
 */
interface UserManager {
    /**
     * Sets the given user ID to the active user. This returns true if the user ID is known, false otherwise.
     */
    suspend fun setActiveUser(newActiveUserId: String): Boolean

    /**
     * Returns the active user. If there is no active user, this will return null.
     */
    suspend fun getActiveUser(): User?

    /**
     * Provides a flow of the [UserState].  Observe this for changes related to users added, removed, and active users updated.
     *
     */
    fun getUserState(): Flow<UserState>
}

@ExperimentalCoroutinesApi
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealUserManager @Inject constructor(
    private val skeletonAuthInteractor: SkeletonAuthInteractor,
    private val activeUserManager: ActiveUserManager,
) : UserManager {

    private val mutex = Mutex()
    private val activeUserState = MutableStateFlow<String?>(null)

    override suspend fun setActiveUser(newActiveUserId: String): Boolean {
        return mutex.withLock {
            val prevActiveUserId = activeUserManager.getActiveUserId()
            if (newActiveUserId != prevActiveUserId && hasUser(newActiveUserId)) {
                activeUserManager.setActiveUser(newActiveUserId)
                activeUserState.emit(newActiveUserId)
                true
            } else {
                false
            }
        }
    }

    private suspend fun hasUser(userId: String): Boolean =
        getUserState().first().users.any { account -> account.userId == userId }

    override suspend fun getActiveUser(): User? =
        getUserState().first().users.firstOrNull { account -> account.isActiveUser }

    // TODO default to first user active if non is currently set
    override fun getUserState(): Flow<UserState> =
        skeletonAuthInteractor.observeAlLUsers().combine(activeUserState) { usersInput, _ ->
            // Find the active user ID and set at most 1 user to being active
            val activeUserId = activeUserManager.getActiveUserId()
            val result = mutableSetOf<User>()
            usersInput.mapTo(result) { userInput ->
                User(userInput.userId, userInput.accessToken, activeUserId == userInput.userId)
            }

            // Handle a case where the active user has been removed from the users
            if (usersInput.none { it.userId == activeUserId }) {
                activeUserManager.clearActiveUser()
            }

            result
        }
            .scan(UserState(emptySet())) { prev, next ->
                val usersRemoved = prev.users.minusById(next)
                val usersAdded = next.minusById(prev.users)
//                        val activeUserUpdate = findActiveUserChange(prev.users, next)

                UserState(next, usersAdded, usersRemoved) // , activeUserUpdate)
            }
            .drop(1)

//    internal fun findActiveUserChange(prev: Set<User>, next: Set<User>): ActiveUserUpdate? {
//        val prevActiveUserId = prev.getActiveUserId()
//        val newActiveUserId = next.getActiveUserId()
//        return if (prevActiveUserId != newActiveUserId) {
//            ActiveUserUpdate(newActiveUserId, prevActiveUserId)
//        } else {
//            null
//        }
//    }
}

internal fun Set<User>.minusById(elements: Set<User>): Set<User> {
    val result = toMutableSet()
    myForEach { item ->
        if (elements.any { item.userId == it.userId }) {
            result.remove(item)
        }
    }
    return result
}

inline fun <T> Iterable<T>.myForEach(action: (T) -> Unit) {
    for (element in this) action(element)
}
