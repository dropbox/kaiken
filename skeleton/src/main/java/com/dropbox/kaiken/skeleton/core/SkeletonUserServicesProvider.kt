package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.scoping.UserServicesProvider

interface SkeletonUserServicesProvider : UserServicesProvider {
    fun initUserServicesOf(user: SkeletonUser)
    fun teardownUserServicesOf(userId: String)
}
