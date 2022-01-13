package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.core.SkeletonUser
import kotlinx.coroutines.flow.Flow

/**
 * Provides a stream of active users to be fed into [UserServices]
 */
interface UserSupplier {
    /**
     * A flow delivering a set of all users authenticated within an app at a given time.  This should
     * emit every time a user is added or removed from the app.
     */
    fun users(): Flow<Set<SkeletonUser>>
}
