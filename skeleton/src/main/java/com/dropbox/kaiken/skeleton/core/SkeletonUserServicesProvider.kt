package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.UserServicesProvider

interface SkeletonUserServicesProvider : UserServicesProvider {
    fun initUserServicesOf(user: SkeletonUser): KaikenUserServices
    fun teardownUserServicesOf(userId: String)
}
