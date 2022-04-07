@file:Suppress("TooManyFunctions")

package com.dropbox.kaiken.skeleton.dagger

import com.dropbox.common.inject.SkeletonScope
import com.dropbox.kaiken.skeleton.core.SkeletonConfig
import com.dropbox.kaiken.skeleton.core.SkeletonOwnerApplication
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component

/**
 * Base Interface for the AppSkeleton SDK Component/Graph
 */
@ContributesTo(SkeletonScope::class)
interface SdkSpec {
    fun getSkeletonConfig(): SkeletonConfig
}

interface SkeletonComponent : SdkSpec
