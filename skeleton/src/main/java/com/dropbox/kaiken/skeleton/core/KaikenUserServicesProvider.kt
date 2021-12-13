package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.cast
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Skeleton implementation of [UserServicesProvider] that registers itself with a [UserStore].
 *
 */
@OptIn(InternalCoroutinesApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class KaikenUserServicesProvider
constructor(
    private val applicationServices: AppServices,
    private val userServicesFactory: (AppServices, SkeletonUser) -> UserServices,
) : SkeletonUserServicesProvider {
    private lateinit var userServices: Flow<Map<String, KaikenUserServices>>
    private val mutex = Mutex(true)

    init {
        // TODO Mike figure out if we want another scope to launch from
        applicationServices.cast<CoroutineScopeBindings>().coroutineScopes().mainScope.launch {
            userServices = (applicationServices as KaikenAppServices).userStore().getUserEvents()
                .scan(emptyMap<String, KaikenUserServices>()) { prev, next ->
                    val result = mutableMapOf<String, KaikenUserServices>()
                    next.usersRemoved.forEach { user ->
                        prev[user.userId]?.getUserTeardownHelper()?.teardown()
                    }

                    next.usersAdded.forEach { user ->
                        val services = userServicesFactory(applicationServices, SkeletonUser(user.userId, user.accessToken)) as KaikenUserServices
                        result[user.userId] = services
                    }

                    result
                }
                .drop(1)
            mutex.unlock()
            userServices.collect()
        }
    }

    override fun provideUserServicesOf(userId: String): UserServices? {
        return runBlocking {
            provideUserServices(userId)
        }
    }

    suspend fun provideUserServices(userId: String): UserServices? =
        mutex.withLock {
            userServices.first()[userId]
        }
}
