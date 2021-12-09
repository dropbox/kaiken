package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.UserServicesProvider
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.dependencymanagement.SkeletonScopedServices

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
