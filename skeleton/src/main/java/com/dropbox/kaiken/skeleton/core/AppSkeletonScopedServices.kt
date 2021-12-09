package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.dependencymanagement.SkeletonScopedServices
import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.UserServicesProvider

class AppSkeletonScopedServices internal  constructor(
    override val component: SdkSpec
) : SkeletonScopedServices {

    override val userServicesFactory = { appServices: AppServices, user: SkeletonUser ->
        component.getSkeletonConfig().scopedServicesFactory.createUserServices(
            appServices,
            user
        )
    }

    override lateinit var userServicesProvider: UserServicesProvider

    override fun provideAppServices(): AppServices = component.getSkeletonConfig()
        .scopedServicesFactory
        .createAppServices(
            component
        )

    override fun provideUserServicesOf(userId: String): UserServices? =
        userServicesProvider.provideUserServicesOf(userId)
}
