package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.core.AppSpecificScopedServicesFactory
import com.dropbox.kaiken.skeleton.core.KaikenConfig
import com.dropbox.kaiken.skeleton.core.SkeletonConfig
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.scoping.AppComponent
import com.dropbox.kaiken.skeleton.scoping.SkeletonScope
import com.dropbox.kaiken.skeleton.scoping.UserParentComponent
import com.dropbox.kaiken.skeleton.scoping.cast
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(SkeletonScope::class, replaces = [KaikenConfig::class])
class FakeSkeletonConfig @Inject constructor() : SkeletonConfig {
    override val scopedServicesFactory: AppSpecificScopedServicesFactory
        get() = object : AppSpecificScopedServicesFactory {
            override fun createAppServices(sdkSpec: SdkSpec): AppServices =
                sdkSpec
                    .cast<AppComponent.AppParentComponent>()
                    .appComponent()

            override fun createUserServices(
                appServices: AppServices,
                user: SkeletonUser
            ): UserServices =
                appServices
                    .cast<UserParentComponent>()
                    .createUserComponent()
                    .userComponent(user.userId.toInt())
        }
}
