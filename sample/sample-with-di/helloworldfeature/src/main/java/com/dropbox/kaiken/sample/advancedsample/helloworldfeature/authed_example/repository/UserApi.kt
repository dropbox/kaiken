package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.UserProfile
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.delay
import javax.inject.Inject

interface UserApi {
    suspend fun getList(): List<String>
}

@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealUserApi @Inject constructor(
    val userProfile: UserProfile
) : UserApi {
    override suspend fun getList(): List<String> {
        delay(3000)
        return listOf(userProfile.userId)
    }
}
