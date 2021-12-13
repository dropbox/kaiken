package com.dropbox.kaiken.sample.advancedsample.app

import android.app.Application
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.DiSampleUser
import com.dropbox.kaiken.skeleton.core.SkeletonOwnerApplication
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.SkeletonScope
import com.dropbox.kaiken.skeleton.scoping.UserScope
import com.dropbox.kaiken.skeleton.scoping.cast
import com.dropbox.kaiken.skeleton.usermanagement.User
import com.dropbox.kaiken.skeleton.usermanagement.UserDataSource
import com.dropbox.kaiken.skeleton.usermanagement.UserStore
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class AdvancedKaikenSampleApplication : SkeletonOwnerApplication() {

    override fun getSdkSpec(): SdkSpec =
        DaggerSkeletonComponent.factory().create(this) as DaggerSkeletonComponent

    @Inject
    lateinit var userFlow: @JvmSuppressWildcards MutableSharedFlow<DiSampleUser>

    @Inject
    lateinit var userStore: UserStore

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
    fun provideUserFlow(): @JvmSuppressWildcards MutableSharedFlow<DiSampleUser> =
        MutableSharedFlow(replay = 1)

    @Provides
    @SingleIn(AppScope::class)
    fun provideUserDataSource(): UserDataSource<DiSampleUser> = object : UserDataSource<DiSampleUser> {
        override suspend fun add(user: DiSampleUser): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun remove(userId: String): Boolean {
            TODO("Not yet implemented")
        }

        override fun getAllUsers(): Flow<Set<DiSampleUser>> {
            TODO("Not yet implemented")
        }
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideUserDataSourceGeneric(userDataSource: UserDataSource<DiSampleUser>): UserDataSource<out User> = userDataSource
}
