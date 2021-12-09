package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.skeleton.core.AppSkeletonInitializer
import com.dropbox.kaiken.skeleton.core.AppSkeletonScopedServices
import com.dropbox.kaiken.skeleton.core.SkeletonOwner
import com.dropbox.kaiken.skeleton.dagger.SdkSpec

class FakeSkeletonOwner(private val spec: SdkSpec) : SkeletonOwner {
    override fun getSdkSpec(): SdkSpec = spec
    override var appSkeleton: AppSkeletonScopedServices =
        AppSkeletonInitializer.init(getSdkSpec()).appSkeletonDelegate

    init {
        appSkeleton
    }
}
