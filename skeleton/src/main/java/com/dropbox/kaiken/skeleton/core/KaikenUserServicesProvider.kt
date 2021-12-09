package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.cast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Skeleton implementation of [UserServicesProvider] that registers itself with a [UserManager].
 *
 */
@OptIn(InternalCoroutinesApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class KaikenUserServicesProvider
constructor(
    private val applicationServices: AppServices,
    private val userServicesFactory: (AppServices, SkeletonUser) -> UserServices,
) : SkeletonUserServicesProvider {
    // better way to handle init?
    private lateinit var userServices: Flow<Map<String, KaikenUserServices>>

    init {
        // TODO Mike figure out if we want another scope to launch from
        applicationServices.cast<CoroutineScopeBindings>().coroutineScopes().mainScope.launch {
            (applicationServices as KaikenAppServices).userManager().getUserState()
                .collect { userState ->
                    userState.usersRemoved.forEach { user ->
                        teardownUserServicesOf(user.userId)
                    }

                    next.usersAdded.forEach { user ->
                        val services = userServicesFactory(applicationServices, SkeletonUser(user.userId, user.accessToken)) as KaikenUserServices
                        result[user.userId] = services
                    }

                    result
                }
                .drop(1)
            userServices.collect()
        }
    }

    override fun provideUserServicesOf(userId: String): UserServices? {
        return runBlocking {
            provideUserServices(userId)
        }
    }

    suspend fun provideUserServices(userId: String): UserServices? {
        return userServices.firstOrNull()?.get(userId)
    }
}
