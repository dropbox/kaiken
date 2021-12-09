package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.kaiken.skeleton.core.AppSpecificScopedServicesFactory
import com.dropbox.kaiken.skeleton.core.KaikenConfig
import com.dropbox.kaiken.skeleton.core.SkeletonAccessTokenPair
import com.dropbox.kaiken.skeleton.core.SkeletonConfig
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.usermanagement.auth.ActiveUserManager
import com.dropbox.kaiken.skeleton.usermanagement.auth.RealActiveUserManager
import com.google.common.truth.Truth.assertThat
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.Component
import org.junit.Test
import javax.inject.Inject

class ScopesConstructionTests {
    @Test
    fun constructScopes() {
        val skeletonServics = DaggerSkeletonTestComponent.factory().create()
        val appServices: AppServices =
            skeletonServics.getSkeletonConfig().scopedServicesFactory.createAppServices(
                skeletonServics
            )
        val userServices: UserServices =
            skeletonServics.getSkeletonConfig().scopedServicesFactory.createUserServices(
                appServices,
                SkeletonUser("1", SkeletonAccessTokenPair("1", "1"), isActiveUser = true)
            )
        val authActivityComponent: AuthRequiredActivityComponent =
            userServices.cast<AuthRequiredActivityComponent.ParentComponent>()
                .createAuthRequiredComponent()
        val noAuthActivityComponent: AuthOptionalActivityComponent =
            appServices.cast<AuthOptionalActivityComponent.ParentComponent>()
                .createAuthOptionalComponent()
        assertThat(
            authActivityComponent.cast<UserTeardownHelperProvider>().userTeardownHelper()
        ).isInstanceOf(UserTeardownHelper::class.java)
        assertThat(
            noAuthActivityComponent.cast<AppTeardownHelperProvider>().appTeardownHelper()
        ).isInstanceOf(AppTeardownHelper::class.java)
    }
}

@MergeComponent(SkeletonScope::class)
@SingleIn(SkeletonScope::class)
interface SkeletonTestComponent : SdkSpec {
    @Component.Factory
    interface Factory {
        fun create(): SkeletonTestComponent
    }
}

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

// You can implement your own teardown logic here.
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class NoOpTeardownHelper @Inject constructor() : AppTeardownHelper {
    override fun teardown() {
        // No op
    }
}

@ContributesBinding(AppScope::class, replaces = [RealActiveUserManager::class])
class FakeActiveUserManager @Inject constructor() : ActiveUserManager {
    override suspend fun clearActiveUser(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setActiveUser(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getActiveUserId(): String? {
        TODO("Not yet implemented")
    }
}

// You can implement your own teardown logic here.
@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class NoOpUserTeardownHelper @Inject constructor() : UserTeardownHelper {
    override fun teardown() {
        // No op
    }
}

@ContributesTo(AuthRequiredActivityScope::class)
interface UserTeardownHelperProvider {
    fun userTeardownHelper(): UserTeardownHelper
//    fun appTeardownHelper():AppTeardownHelper
}

@ContributesTo(AuthOptionalActivityScope::class)
interface AppTeardownHelperProvider {
    fun appTeardownHelper(): AppTeardownHelper
}
