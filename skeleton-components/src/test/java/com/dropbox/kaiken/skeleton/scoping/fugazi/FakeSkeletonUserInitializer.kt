package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.skeleton.initializers.UserServicesInitializer
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject


@ContributesMultibinding(UserScope::class)
@SingleIn(UserScope::class)
class FakeSkeletonUserInitializer @Inject constructor() : UserServicesInitializer {
    var initUserCalled = 0
    override fun initUser(userId: String) {
        initUserCalled += 1
    }

    var userTearDownCalled = 0
    override fun userTeardown(userId: String) {
        userTearDownCalled += 1
    }
}