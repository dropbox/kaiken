package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.dagger.SkeletonComponent
import com.dropbox.kaiken.skeleton.dependencymanagement.SkeletonScopedServices
import com.dropbox.kaiken.skeleton.scoping.cast

class AppSkeletonInitializer(val appSkeletonDelegate: AppSkeletonScopedServices) :
    SkeletonScopedServices by appSkeletonDelegate {

    init {
        userServicesProvider = with(provideAppServices() as KaikenAppServices) {
            KaikenUserServicesProvider(
                this,
                userServicesFactory,
                userStore().getUserEvents(),
                cast<CoroutineScopeBindings>().coroutineScopes().mainScope, // TODO Mike figure out if we want another scope to launch from
            )
        }
    }

    companion object {
        internal lateinit var appSkeleton: AppSkeletonInitializer

        fun init(
            component: SkeletonComponent,
        ): AppSkeletonInitializer {
            check(!this::appSkeleton.isInitialized)
            appSkeleton = AppSkeletonInitializer(AppSkeletonScopedServices(component))
            return appSkeleton
        }

        /**
         * WARNING
         *
         * As tempting as it may be. DO NOT ever increase the visibility of this method. No one should be able to
         * directly obtain an instance of this object except for Skeleton code itself.
         *
         * WARNING
         */
        fun getInstance(): AppSkeletonInitializer {
            check(this::appSkeleton.isInitialized)
            return appSkeleton
        }
    }
}
