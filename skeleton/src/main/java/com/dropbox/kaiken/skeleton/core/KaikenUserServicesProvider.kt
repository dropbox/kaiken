package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.initializers.UserServicesInitializerProvider
import com.dropbox.kaiken.skeleton.scoping.cast
import com.dropbox.kaiken.skeleton.usermanagement.UsersEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Skeleton implementation of [SkeletonUserServicesProvider] that registers itself with a [UserStore].
 *
 */
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class KaikenUserServicesProvider(
    private val applicationServices: AppServices,
    private val userServicesFactory: (AppServices, SkeletonUser) -> UserServices,
    userEvents: Flow<UsersEvent>,
    coroutineScope: CoroutineScope,
) : SkeletonUserServicesProvider {
    private var userServices: Map<String, KaikenUserServices> = emptyMap()
    private val mutex = Mutex(true)

    init {
        coroutineScope.launch {
            userEvents
                .scan<UsersEvent, Map<String, KaikenUserServices>>(emptyMap()) { prev, next ->
                    val result = prev.toMutableMap()
                    next.usersRemoved.forEach { user ->
                        result.remove(user.userId)
                                ?.cast<UserServicesInitializerProvider>()
                                ?.userServicesInitializers
                                ?.forEach { userServicesInitializer -> userServicesInitializer.userTeardown(user.userId)  }
                    }

                    next.usersAdded.forEach { user ->
                        with (userServicesFactory(applicationServices, user) as KaikenUserServices) {
                            result[user.userId] = this
                            cast<UserServicesInitializerProvider>()
                                    .userServicesInitializers
                                    .forEach { it.initUser(user.userId) }
                        }
                    }

                    result
                }
                .drop(1)
                .onEach {
                    userServices = it
                    if (mutex.isLocked) {
                        mutex.unlock()
                    }
                }
                .collect()
        }
    }

    override fun provideUserServicesOf(userId: String): UserServices? =
        runBlocking {
            provideUserServices(userId)
        }

    suspend fun provideUserServices(userId: String): UserServices? =
        mutex.withLock {
            userServices[userId]
        }
}
