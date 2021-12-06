package com.dropbox.kaiken.sample.advancedsample.app

import android.app.Application
import com.dropbox.kaiken.skeleton.skeleton.core.SkeletonApplication
import com.dropbox.kaiken.skeleton.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.skeleton.usermanagement.UserManager
import com.dropbox.kaiken.scoping.AppScope
import com.dropbox.kaiken.scoping.SingleIn
import com.dropbox.kaiken.scoping.SkeletonScope
import com.dropbox.kaiken.scoping.UserScope
import com.dropbox.kaiken.scoping.cast
import com.dropbox.kaiken.skeleton.skeleton.usermanagement.auth.UserInput
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


class AdvancedKaikenSampleApplication : SkeletonApplication() {

    override fun getSdkSpec(): SdkSpec = DaggerSkeletonComponent.factory().create(this) as DaggerSkeletonComponent

    @Inject
    lateinit var userFlow: @JvmSuppressWildcards MutableSharedFlow<UserInput>

    @Inject
    lateinit var userManager: UserManager

    override fun onCreate() {
        super.onCreate()
        appServices.cast<ApplicationInjector>().inject(this)
        // Let's "log-in" users for sample purposes. In a real application this code would definitely not live
        // here and would be way way more complex
           GlobalScope.launch {
               //this user flow is references in [SkeletonAuthInteractor]
               // which is a simplified version of an UserStore/Account Manager
               //normally the
               userFlow.emit(UserInput("1", "Mike"))
               userManager.setActiveUser("1")

           }
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
    fun provideSkeletonApplication(application: Application):SkeletonApplication= application as SkeletonApplication
}


@MergeComponent(SkeletonScope::class)
@SingleIn(SkeletonScope::class)
interface SkeletonComponent : SdkSpec {
    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance app: Application,
        ): SkeletonComponent
    }
}
