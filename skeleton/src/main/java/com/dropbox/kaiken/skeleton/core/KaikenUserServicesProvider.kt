package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.cast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Skeleton implementation of [UserServicesProvider] that registers itself with a [UserManager].
 *
 */
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
                            SkeletonUser(
                                user.userId,
                                SkeletonOauth2(user.accessToken),
                                user.isActiveUser
                            )
                        )
                    }
                }
        }
    }

    override fun initUserServicesOf(user: SkeletonUser) {
        synchronized(userServicesMap) {
            val userServices: KaikenUserServices =
                userServicesFactory(applicationServices, user) as KaikenUserServices
            userServicesMap[user.userId] = userServices
        }
    }

    override fun teardownUserServicesOf(userId: String) {
        synchronized(userServicesMap) {
            userServicesMap.remove(userId)?.getUserTeardownHelper()?.teardown()
        }
    }

    override fun provideUserServicesOf(userId: String): UserServices? {
        synchronized(userServicesMap) {
            return userServicesMap[userId]
        }
    }
}
