package com.dropbox.kaiken.skeleton.scoping

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
