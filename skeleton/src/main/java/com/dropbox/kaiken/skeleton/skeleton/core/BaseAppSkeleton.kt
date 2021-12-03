package com.dropbox.kaiken.skeleton.skeleton.core

import com.dropbox.kaiken.skeleton.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.skeleton.dependencymanagement.SkeletonScopedServices
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserServicesProvider

class AppSkeletonDelegate(
    override val component: SdkSpec
) : SkeletonScopedServices {

    override val appServices: AppServices = component.getSkeletonConfig()
        .scopedServicesFactory
        .createAppServices(
            component
        )

    override val userServicesFactory = { appServices: AppServices, user: SkeletonUser ->
        component.getSkeletonConfig().scopedServicesFactory.createUserServices(
            appServices,
            user
//            component.getSkeletonConfig().appInfoProvider
        )
    }

    override lateinit var userServicesProvider: UserServicesProvider

    override fun provideAppServices(): AppServices = appServices

    override fun provideUserServicesOf(userId: String): UserServices? =
        userServicesProvider.provideUserServicesOf(userId)
}
