package com.dropbox.kaiken.skeleton.core


abstract class SkeletonOwner : BaseSkeletonApplication() {

    override fun onCreate() {
        super.onCreate()
        AppSkeletonInitializer.init(getSdkSpec())
        appSkeleton = AppSkeletonInitializer.getInstance().appSkeletonDelegate
    }
}
