package com.dropbox.kaiken.skeleton.skeleton.core

import com.dropbox.kaiken.skeleton.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.scoping.AppComponent
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.SkeletonScope
import com.dropbox.kaiken.scoping.UserComponent
import com.dropbox.kaiken.scoping.UserParentComponent
import com.dropbox.kaiken.scoping.cast
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(SkeletonScope::class)
class KaikenConfig @Inject constructor(val app: SkeletonApplication) : SkeletonConfig {
    override val scopedServicesFactory: AppSpecificScopedServicesFactory
        get() = object : AppSpecificScopedServicesFactory {
            override fun createAppServices(sdkSpec: SdkSpec): AppComponent =
                    app.getSdkSpec().cast<AppComponent.AppParentComponent>().appComponent()

            override fun createUserServices(
                    appServices: AppServices,
                    user: SkeletonUser,
//                    appInfoProvider: AppInfoProvider,
            ): UserComponent = appServices.cast<UserParentComponent>().createUserComponent()
                    .userComponent(userId = user.userId.toInt())
        }
}