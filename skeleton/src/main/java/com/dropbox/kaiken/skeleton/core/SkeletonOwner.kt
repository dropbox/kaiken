package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserServicesProvider
import com.dropbox.kaiken.skeleton.dagger.SkeletonComponent
import com.dropbox.kaiken.skeleton.dependencymanagement.SkeletonScopedServices

interface SkeletonOwner : SkeletonScopedServices {

    var scopedServices: AppSkeletonScopedServices

    override val component: SkeletonComponent
        get() = scopedServices.component

    fun getSkeletonComponent(): SkeletonComponent

    override fun provideAppServices() = scopedServices.provideAppServices()

    override fun provideUserServicesOf(userId: String) =
        scopedServices.provideUserServicesOf(userId)

    override val userServicesFactory: (AppServices, SkeletonUser) -> UserServices
        get() = scopedServices.userServicesFactory

    override var userServicesProvider: UserServicesProvider
        get() = scopedServices.userServicesProvider
        set(value) {
            scopedServices.userServicesProvider = value
        }
}
