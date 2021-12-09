package com.dropbox.kaiken.skeleton.dependencymanagement

import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.ScopedServicesProvider
import com.dropbox.kaiken.skeleton.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.UserServicesProvider
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.dagger.SdkSpec

interface SkeletonScopedServices : ScopedServicesProvider {
    val appServices: AppServices
    val userServicesFactory: (AppServices, SkeletonUser) -> UserServices
    var userServicesProvider: UserServicesProvider
    val component: SdkSpec
}
