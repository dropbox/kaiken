package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.core.SkeletonUser

interface SkeletonMapper<T> {
    fun toSkeletonUser(from: T): SkeletonUser
}
