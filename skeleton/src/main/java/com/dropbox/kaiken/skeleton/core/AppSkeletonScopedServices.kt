package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserServicesProvider
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.dependencymanagement.SkeletonScopedServices
import com.dropbox.kaiken.skeleton.initializers.AppInitializerProvider
import com.dropbox.kaiken.skeleton.scoping.cast

class AppSkeletonScopedServices constructor(
    override val component: SdkSpec
) : SkeletonScopedServices {

    private val appServices: AppServices = component.getSkeletonConfig()
                .scopedServicesFactory
                .createAppServices(
                        component
                ).also {
                    it.cast<AppInitializerProvider>()
                            .appServicesInitializers
                            .forEach { it.init() }
                }

    override fun provideAppServices(): AppServices = appServices

    override val userServicesFactory = { appServices: AppServices, user: SkeletonUser ->
        component.getSkeletonConfig().scopedServicesFactory.createUserServices(
                appServices,
                user
        )
    }

    override lateinit var userServicesProvider: UserServicesProvider

    override fun provideUserServicesOf(userId: String): UserServices? =
            userServicesProvider.provideUserServicesOf(userId)
}
