package com.dropbox.kaiken.skeleton.core

import com.dropbox.common.inject.SkeletonScope
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.skeleton.components.scoping.AppComponent
import com.dropbox.kaiken.skeleton.components.scoping.UserComponent
import com.dropbox.kaiken.skeleton.components.scoping.UserParentComponent
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.cast
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(SkeletonScope::class)
@SingleIn(SkeletonScope::class)
class KaikenConfig @Inject constructor() : SkeletonConfig {
    override val scopedServicesFactory: AppSpecificScopedServicesFactory
        get() = object : AppSpecificScopedServicesFactory {
            override fun createAppServices(sdkSpec: SdkSpec): AppComponent =
                sdkSpec.cast<AppComponent.AppParentComponent>().appComponent()

            override fun createUserServices(
                appServices: AppServices,
                user: SkeletonUser,
            ): UserComponent = appServices.cast<UserParentComponent>().createUserComponent()
                .userComponent(user)
        }
}
