package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.common.inject.AppScope
import com.dropbox.common.inject.SkeletonScope
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.usermanagement.UserSupplier
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@MergeComponent(SkeletonScope::class)
@SingleIn(SkeletonScope::class)
interface SkeletonTestComponent : SdkSpec {
    @Component.Factory
    interface Factory {
        fun create(): SkeletonTestComponent
    }
}

@Module
@ContributesTo(AppScope::class)
class BindingModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideUserMapper(): UserSupplier = DummyUserSupplier(flow { })
}

open class DummyUserSupplier constructor(private val users: Flow<Set<SkeletonUser>>) : UserSupplier {
    override fun users(): Flow<Set<SkeletonUser>> = users
}
