package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.components.scoping.AppComponent
import com.dropbox.kaiken.skeleton.core.KaikenConfig
import com.dropbox.kaiken.skeleton.core.SkeletonAccessTokenPair
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.initializers.AppInitializerProvider
import com.dropbox.kaiken.skeleton.scoping.fugazi.FakeSkeletonAppInitializer
import com.dropbox.kaiken.skeleton.scoping.fugazi.FakeSkeletonOwner
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SkeletonOwnerTests {
    @Test
    fun testSkeletonOwner() {
        val skeletonServices = DaggerSkeletonTestComponent.factory().create()
        val skeleton = FakeSkeletonOwner(skeletonServices).scopedServices
        val appServices = skeleton.provideAppServices()
        assertThat(appServices).isInstanceOf(AppComponent::class.java)
        val userServicesFactory = skeleton.userServicesFactory
        val userServices: UserServices = userServicesFactory(
            appServices, SkeletonUser("1", SkeletonAccessTokenPair("1", "1"))
        )
        assertThat(userServices).isNotNull()

        val fakeAppInit = appServices.cast<AppInitializerProvider>().appServicesInitializers.filterIsInstance<FakeSkeletonAppInitializer>().first()
        assertThat(fakeAppInit.initCalled).isEqualTo(1)
    }

    @Test
    fun testKaikenConfig() {
        val skeletonServices = DaggerSkeletonTestComponent.factory().create()

        val config = KaikenConfig()
        val appServices: AppServices =
            config.scopedServicesFactory.createAppServices(skeletonServices)
        assertThat(appServices).isInstanceOf(AppComponent::class.java)
    }
}
