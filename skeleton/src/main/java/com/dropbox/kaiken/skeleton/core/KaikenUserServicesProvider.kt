package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.usermanagement.UsersEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
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
                    println(next.toString())
                    next.usersRemoved.forEach { user ->
                        println(user.toString())
                        result.remove(user.userId)?.getUserTeardownHelper()?.teardown()
                    }

                    next.usersAdded.forEach { user ->
                        result[user.userId] =
                            userServicesFactory(applicationServices, user) as KaikenUserServices
                    }

                    println(result)
                    result
                }
                .drop(1)
                .collect {
                    userServices = it
                    mutex.unlock()
                }
        }
    }

    override fun provideUserServicesOf(userId: String): UserServices? {
        return runBlocking {
            provideUserServices(userId)
        }
    }

    suspend fun provideUserServices(userId: String): UserServices? =
        mutex.withLock {
            userServices[userId]
        }
}
