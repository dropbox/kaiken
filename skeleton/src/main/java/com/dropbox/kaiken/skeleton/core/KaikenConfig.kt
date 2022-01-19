package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.scoping.AppComponent
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.SkeletonScope
import com.dropbox.kaiken.skeleton.scoping.UserComponent
import com.dropbox.kaiken.skeleton.scoping.UserParentComponent
import com.dropbox.kaiken.skeleton.scoping.cast
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import javax.inject.Inject

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

@ContributesTo(SkeletonScope::class)
@Module
abstract class KaikenConfigBinding {
    @SingleIn(SkeletonScope::class)
    @Binds
    abstract fun bindKaikenConfigBinding(real: KaikenConfig): SkeletonConfig
}
