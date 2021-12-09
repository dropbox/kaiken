package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.squareup.anvil.annotations.MergeComponent
import dagger.Component

@MergeComponent(SkeletonScope::class)
@SingleIn(SkeletonScope::class)
interface SkeletonTestComponent : SdkSpec {
    @Component.Factory
    interface Factory {
        fun create(): SkeletonTestComponent
    }
}
