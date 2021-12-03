package com.dropbox.kaiken.skeleton.skeleton.core

abstract class SkeletonApplication : BaseSkeletonApplication() {
    override fun onCreate() {
        super.onCreate()
        AppSkeletonInitializer.init(getSdkSpec())
        appSkeleton = AppSkeletonInitializer.getInstance().appSkeletonDelegate
    }
}

