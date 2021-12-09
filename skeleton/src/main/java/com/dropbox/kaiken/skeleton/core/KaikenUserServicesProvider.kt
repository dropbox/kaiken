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
@OptIn(InternalCoroutinesApi::class)
class KaikenUserServicesProvider
constructor(
    private val applicationServices: AppServices,
    private val userServicesFactory: (AppServices, SkeletonUser) -> UserServices,
) : SkeletonUserServicesProvider {
    private val userServicesMap = mutableMapOf<String, KaikenUserServices>()

    init {
        // TODO Mike figure out if we want another scope to launch from
        applicationServices.cast<CoroutineScopeBindings>().coroutineScopes().mainScope.launch {
            (applicationServices as KaikenAppServices).userManager().getUserState()
                .collect { userState ->
                    userState.usersRemoved.forEach { user ->
                        teardownUserServicesOf(user.userId)
                    }
                    // TODO Mike figure out what we want to do for the base user type
                    userState.usersAdded.forEach { user ->
                        initUserServicesOf(
                            SkeletonUser(user.userId, user.accessToken)
                        )
                    }
                }
        }
    }

    private val userManager = (applicationServices as KaikenAppServices).userStore()

    override fun initUserServicesOf(user: SkeletonUser): KaikenUserServices =
        with(userServicesFactory(applicationServices, user) as KaikenUserServices) {
            userServicesMap[user.userId] = this
            this
        }

    override fun teardownUserServicesOf(userId: String) {
        userServicesMap.remove(userId)?.getUserTeardownHelper()?.teardown()
    }

    override fun provideUserServicesOf(userId: String): UserServices? {
        return runBlocking {
            provideUserServices(userId)
        }
    }

    suspend fun provideUserServices(userId: String): UserServices? {
        with(userServicesMap[userId]) {
            if (this != null) {
                return this
            }
        }

        return userManager.getUserById(userId)?.let {
            initUserServicesOf(SkeletonUser(it.userId, it.accessToken))
        }
    }
}
