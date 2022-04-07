package com.dropbox.kaiken.skeleton.dependencymanagement

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.ScopedServicesProvider
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserServicesProvider
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.dagger.SkeletonComponent

interface SkeletonScopedServices : ScopedServicesProvider {
    val userServicesFactory: (AppServices, SkeletonUser) -> UserServices
    var userServicesProvider: UserServicesProvider
    val component: SkeletonComponent
}
