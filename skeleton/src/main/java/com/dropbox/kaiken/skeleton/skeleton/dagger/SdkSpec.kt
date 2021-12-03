@file:Suppress("TooManyFunctions")

package com.dropbox.kaiken.skeleton.skeleton.dagger

import com.dropbox.kaiken.skeleton.skeleton.core.SkeletonConfig
import com.dropbox.kaiken.scoping.SkeletonScope
import com.squareup.anvil.annotations.ContributesTo

/**
 * Base Interface for the AppSkeleton SDK Component/Graph
 */
@ContributesTo(SkeletonScope::class)
interface SdkSpec {
    fun getSkeletonConfig(): SkeletonConfig
}
