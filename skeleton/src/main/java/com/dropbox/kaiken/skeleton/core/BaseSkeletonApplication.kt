package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.dependencymanagement.SkeletonScopedServices
import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.UserServicesProvider

interface SkeletonOwner : SkeletonScopedServices {

    var appSkeleton: AppSkeletonDelegate
    override val component: SdkSpec
        get() = appSkeleton.component

    fun getSdkSpec(): SdkSpec
    override fun provideAppServices() = appSkeleton.provideAppServices()
    override val appServices: AppServices
        get() = provideAppServices()

    override fun provideUserServicesOf(userId: String) = appSkeleton.provideUserServicesOf(userId)
    override val userServicesFactory: (AppServices, SkeletonUser) -> UserServices
        get() = appSkeleton.userServicesFactory

    override var userServicesProvider: UserServicesProvider
        get() = appSkeleton.userServicesProvider
        set(value) {
            appSkeleton.userServicesProvider = value
        }
}

class FakeSkeletonOwner : SkeletonOwner {
    override lateinit var appSkeleton: AppSkeletonDelegate
    lateinit var fakeSdkSpec: SdkSpec
    override fun getSdkSpec(): SdkSpec =
        fakeSdkSpec

    fun initialize() {
        appSkeleton = AppSkeletonInitializer.init(getSdkSpec()).appSkeletonDelegate
    }
}