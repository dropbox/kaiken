package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.common.inject.AppScope
import com.dropbox.kaiken.skeleton.initializers.AppServicesInitializer
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
@SingleIn(AppScope::class)
class FakeSkeletonAppInitializer @Inject constructor() : AppServicesInitializer {
    var initCalled = 0
    override fun init() {
        initCalled += 1
    }

    var teardownCalled = 0
    override fun teardown() {
        teardownCalled += 1
    }
}
