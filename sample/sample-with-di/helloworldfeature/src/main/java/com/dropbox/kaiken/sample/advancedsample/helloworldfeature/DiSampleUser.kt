package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import com.dropbox.kaiken.skeleton.core.SkeletonAccessToken
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.usermanagement.SkeletonMapper

class DiSampleUser(val userId: String, val accessToken: SkeletonAccessToken, val name: String)

class RealSkeletonUserMapper : SkeletonMapper<DiSampleUser> {
    override fun toSkeletonUser(from: DiSampleUser): SkeletonUser {
        return SkeletonUser(from.userId, from.accessToken)
    }
}