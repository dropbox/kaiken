package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.kaiken.skeleton.core.KaikenConfig
import com.dropbox.kaiken.skeleton.core.SkeletonAccessTokenPair
import com.dropbox.kaiken.skeleton.core.SkeletonUser
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
            appServices, SkeletonUser("1", SkeletonAccessTokenPair("1", "1"), isActiveUser = true)
        )
        assertThat(userServices).isNotNull()
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
