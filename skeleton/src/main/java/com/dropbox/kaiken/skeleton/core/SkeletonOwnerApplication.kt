package com.dropbox.kaiken.skeleton.core

import android.app.Application

abstract class SkeletonOwnerApplication : SkeletonOwner, Application() {
    override fun onCreate() {
        super.onCreate()
        scopedServices = AppSkeletonInitializer.init(getSdkSpec()).appSkeletonDelegate
    }

    override lateinit var scopedServices: AppSkeletonScopedServices
}
