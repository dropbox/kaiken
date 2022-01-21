package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
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