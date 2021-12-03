package com.dropbox.kaiken.skeleton.skeleton.core

import android.app.Application
import com.dropbox.kaiken.skeleton.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.skeleton.dependencymanagement.SkeletonScopedServices
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserServicesProvider

abstract class BaseSkeletonApplication : SkeletonScopedServices, Application() {
    protected lateinit var appSkeleton: AppSkeletonDelegate
    override val component: SdkSpec
        get() = appSkeleton.component

    abstract fun getSdkSpec(): SdkSpec
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
