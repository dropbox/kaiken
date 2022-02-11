package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.core.SkeletonAccessTokenPair
import com.dropbox.kaiken.skeleton.core.SkeletonUser
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
        assertThat(userServices).isNotNull()
    }
}
