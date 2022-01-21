package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.AppTeardownHelper
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserTeardownHelper
import com.dropbox.kaiken.skeleton.components.scoping.AuthOptionalComponent
import com.dropbox.kaiken.skeleton.components.scoping.AuthRequiredComponent
import com.dropbox.kaiken.skeleton.core.SkeletonAccessTokenPair
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.scoping.fugazi.AppTeardownHelperProvider
import com.dropbox.kaiken.skeleton.scoping.fugazi.UserTeardownHelperProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ScopesConstructionTests {
    @Test
    fun constructScopes() {
        val skeletonServices = DaggerSkeletonTestComponent.factory().create()
        val appServices: AppServices =
            skeletonServices.getSkeletonConfig().scopedServicesFactory.createAppServices(
                skeletonServices
            )
        val userServices: UserServices =
            skeletonServices.getSkeletonConfig().scopedServicesFactory.createUserServices(
                appServices,
                SkeletonUser("1", SkeletonAccessTokenPair("1", "1"))
            )
        val authComponent: AuthRequiredComponent =
            userServices.cast<AuthRequiredComponent.ParentComponent>()
                .createAuthRequiredComponent()
        val noAuthComponent: AuthOptionalComponent =
            appServices.cast<AuthOptionalComponent.ParentComponent>()
                .createAuthOptionalComponent()
        assertThat(
            authComponent.cast<UserTeardownHelperProvider>().userTeardownHelper()
        ).isInstanceOf(UserTeardownHelper::class.java)
        assertThat(
            noAuthComponent.cast<AppTeardownHelperProvider>().appTeardownHelper()
        ).isInstanceOf(AppTeardownHelper::class.java)
    }
}
