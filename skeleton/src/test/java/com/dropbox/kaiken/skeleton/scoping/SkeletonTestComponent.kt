package com.dropbox.kaiken.skeleton.scoping

import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.usermanagement.DummyUserMapper
import com.dropbox.kaiken.skeleton.usermanagement.UserMapper
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.Component
import dagger.Module
import dagger.Provides
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
    fun provideUserMapper(): UserMapper = DummyUserMapper(flow { })
}
