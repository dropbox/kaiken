package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.dependencymanagement.SkeletonScopedServices
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserServicesProvider

interface SkeletonOwner : SkeletonScopedServices {

    var scopedServices: AppSkeletonScopedServices

    override val component: SdkSpec
        get() = scopedServices.component

    fun getSdkSpec(): SdkSpec

    override fun provideAppServices() = scopedServices.provideAppServices()

    override fun provideUserServicesOf(userId: String) = scopedServices.provideUserServicesOf(userId)
    override val userServicesFactory: (AppServices, SkeletonUser) -> UserServices
        get() = scopedServices.userServicesFactory

    override var userServicesProvider: UserServicesProvider
        get() = scopedServices.userServicesProvider
        set(value) {
            scopedServices.userServicesProvider = value
        }
}
