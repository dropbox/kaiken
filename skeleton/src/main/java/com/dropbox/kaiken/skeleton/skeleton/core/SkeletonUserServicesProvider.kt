package com.dropbox.kaiken.skeleton.skeleton.core

import com.dropbox.kaiken.scoping.TeardownHelper
import com.dropbox.kaiken.scoping.UserServicesProvider

interface SkeletonUserServicesProvider : UserServicesProvider {
    fun initUserServicesOf(user: SkeletonUser)
    fun teardownUserServicesOf(userId: String)
    fun dispatchTeardown(teardownHelper: TeardownHelper)
}
