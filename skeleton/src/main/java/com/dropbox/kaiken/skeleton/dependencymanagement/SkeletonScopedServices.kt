package com.dropbox.kaiken.skeleton.dependencymanagement

import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.dagger.SdkSpec
import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.ScopedServicesProvider
import com.dropbox.kaiken.skeleton.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.UserServicesProvider

interface SkeletonScopedServices : ScopedServicesProvider {
    val userServicesFactory: (AppServices, SkeletonUser) -> UserServices
    var userServicesProvider: UserServicesProvider
    val component: SdkSpec
}