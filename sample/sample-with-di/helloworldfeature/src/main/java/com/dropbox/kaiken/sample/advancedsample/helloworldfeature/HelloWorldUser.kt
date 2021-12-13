package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import com.dropbox.kaiken.skeleton.core.SkeletonAccessToken
import com.dropbox.kaiken.skeleton.usermanagement.User

class HelloWorldUser(override val userId: String, override val accessToken: SkeletonAccessToken) : User
