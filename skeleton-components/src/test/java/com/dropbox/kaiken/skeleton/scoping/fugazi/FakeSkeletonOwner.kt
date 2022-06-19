package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.skeleton.core.AppSkeletonInitializer
import com.dropbox.kaiken.skeleton.core.AppSkeletonScopedServices
import com.dropbox.kaiken.skeleton.core.SkeletonOwner
import com.dropbox.kaiken.skeleton.dagger.SkeletonComponent

class FakeSkeletonOwner(private val spec: SkeletonComponent) : SkeletonOwner {
    override fun getSkeletonComponent(): SkeletonComponent = spec
    override var scopedServices: AppSkeletonScopedServices =
        AppSkeletonInitializer.init(getSkeletonComponent()).appSkeletonDelegate
}
