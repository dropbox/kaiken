package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.kaiken.skeleton.scoping.fugazi.FakeSkeletonOwner
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SkeletonOwnerTests {
    @Test
    fun testSkeletonOwner() {
        val skeletonServices = DaggerSkeletonTestComponent.factory().create()

        val skeleton = FakeSkeletonOwner(skeletonServices).appSkeleton
        val appServices = skeleton.provideAppServices()
        assertThat(appServices).isInstanceOf(AppComponent::class.java)
    }
}