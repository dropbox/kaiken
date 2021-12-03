package com.dropbox.kaiken.skeleton.skeleton.core

import com.dropbox.kaiken.skeleton.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices

/**
 * Factory that allows an Application that uses the AppSkeleton to provide factory methods for its scoped
 * services (i.e [AppServices] and [UserServices].
 *
 * The skeleton is responsible for the instantiation and manages the lifecycles of both app services and user services.
 */
interface AppSpecificScopedServicesFactory {
    /**
     * Creates an instance of the application services for your application.
     *
     * This method will be called once and only once by the Skeleton framework upon its instantiation.
     */
    fun createAppServices(
        sdkSpec: SdkSpec
    ): AppServices

    /**
     * Creates an instance of the user services for your application.
     *
     * This method will be called every time a user is logged-in, and will be destroyed when the user is logged-out.
     */
    //TODO mike make appInfoProvider injectable in xplat
    fun createUserServices(
            appServices: AppServices,
            user: SkeletonUser,
//            appInfoProvider: AppInfoProvider
    ): UserServices
}

interface SkeletonAccessToken

class SkeletonAccessTokenPair(val key: String, val secret: String) : SkeletonAccessToken

data class SkeletonOauth2(
    val accessToken: String
) : SkeletonAccessToken

data class SkeletonUser(val userId: String, val accessToken: SkeletonAccessToken, val isActiveUser: Boolean)
