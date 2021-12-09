package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.scoping.AppComponent
import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.SkeletonScope
import com.dropbox.kaiken.skeleton.scoping.UserComponent
import com.dropbox.kaiken.skeleton.scoping.UserParentComponent
import com.dropbox.kaiken.skeleton.scoping.cast
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(SkeletonScope::class)
class KaikenConfig @Inject constructor() : SkeletonConfig {
    override val scopedServicesFactory: AppSpecificScopedServicesFactory
        get() = object : AppSpecificScopedServicesFactory {
            override fun createAppServices(sdkSpec: SdkSpec): AppComponent =
               sdkSpec.cast<AppComponent.AppParentComponent>().appComponent()

            override fun createUserServices(
                appServices: AppServices,
                user: SkeletonUser,
            ): UserComponent = appServices.cast<UserParentComponent>().createUserComponent()
                .userComponent(userId = user.userId.toInt())
        }
}
