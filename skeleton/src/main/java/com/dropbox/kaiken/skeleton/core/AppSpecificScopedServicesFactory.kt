package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.dagger.SkeletonComponent

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
        skeletonComponent: SkeletonComponent
    ): AppServices

    /**
     * Creates an instance of the user services for your application.
     *
     * This method will be called every time a user is logged-in, and will be destroyed when the user is logged-out.
     */
    // TODO mike make appInfoProvider injectable in xplat
    fun createUserServices(
        appServices: AppServices,
        user: SkeletonUser
    ): UserServices
}

interface SkeletonAccessToken

class SkeletonAccessTokenPair(val key: String, val secret: String) : SkeletonAccessToken

class SkeletonOauth2(
    val accessToken: String
) : SkeletonAccessToken

class SkeletonUser(
    val userId: String,
    val accessToken: SkeletonAccessToken,
)
