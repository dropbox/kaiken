package com.dropbox.kaiken.sample.advancedsample.app

import android.app.Application
import android.util.Log
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.AccountStore
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.DiSampleUser
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealSkeletonUserMapper
import com.dropbox.kaiken.skeleton.core.SkeletonOwnerApplication
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.SkeletonScope
import com.dropbox.kaiken.skeleton.scoping.UserScope
import com.dropbox.kaiken.skeleton.scoping.cast
import com.dropbox.kaiken.skeleton.usermanagement.SkeletonMapper
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdvancedKaikenSampleApplication : SkeletonOwnerApplication() {

    override fun getSdkSpec(): SdkSpec =
        DaggerSkeletonComponent.factory().create(this) as DaggerSkeletonComponent

    override fun onCreate() {
        super.onCreate()
        provideAppServices().cast<ApplicationInjector>().inject(this)
    }
}

@ContributesTo(AppScope::class)
interface ApplicationInjector {
    fun inject(application: AdvancedKaikenSampleApplication)
}

@ContributesTo(UserScope::class)
@Module
class UserModule {
    @Provides
    @SingleIn(UserScope::class)
    fun provideUserProfile(userId: Int) = UserProfile(userId.toString(), userId.toString())
}

@ContributesTo(SkeletonScope::class)
@Module
class SkeletonModule {
    @Provides
    @SingleIn(SkeletonScope::class)
    fun provideApplication(application: SkeletonOwnerApplication): Application =
        application
}

@MergeComponent(SkeletonScope::class)
@SingleIn(SkeletonScope::class)
interface SkeletonComponent : SdkSpec {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: SkeletonOwnerApplication,
        ): SkeletonComponent
    }
}

@Module
@ContributesTo(AppScope::class)
class BindingModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSkeletonMapper(): SkeletonMapper<DiSampleUser> {
        return RealSkeletonUserMapper()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideSkeletonUserFlow(accountStore: AccountStore, mapper: SkeletonMapper<DiSampleUser>): @JvmSuppressWildcards Flow< @JvmSuppressWildcards Set<SkeletonUser>> {
        return accountStore.getAllUsers().map { sampleUsers ->
            sampleUsers.map { diSampleUser ->
                mapper.toSkeletonUser(diSampleUser)
            }.toSet()
        }
    }
}
