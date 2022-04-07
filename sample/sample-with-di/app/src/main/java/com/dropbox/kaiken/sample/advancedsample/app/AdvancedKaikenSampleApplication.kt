package com.dropbox.kaiken.sample.advancedsample.app

import android.app.Application
import android.util.Log
import com.dropbox.common.inject.AppScope
import com.dropbox.common.inject.SkeletonScope
import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.TimeMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.UserProfile
import com.dropbox.kaiken.skeleton.core.SkeletonConfig
import com.dropbox.kaiken.skeleton.core.SkeletonOwnerApplication
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.dagger.SkeletonComponent
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.cast
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject

class AdvancedKaikenSampleApplication : SkeletonOwnerApplication() {

    @Inject
    lateinit var skeletonConfig: SkeletonConfig

    @Inject
    lateinit var time: TimeMessageProvider

    override fun getSkeletonComponent(): SkeletonComponent =
        DaggerRealComponent.factory().create(this)

    override fun onCreate() {
        super.onCreate()
        provideAppServices().cast<ApplicationInjector>().inject(this)
        Log.d("TAG","Skeleton Config is ${skeletonConfig}")
        Log.d("TAG","The time is ${time.tellTheTime()}")
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
    fun provideUserProfile(user: SkeletonUser) = UserProfile(user.userId, user.userId)
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
interface RealComponent : SkeletonComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: SkeletonOwnerApplication,
        ): RealComponent
    }
}