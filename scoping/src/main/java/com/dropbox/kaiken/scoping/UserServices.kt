package com.dropbox.kaiken.scoping

import com.dropbox.common.inject.AUserScope
import com.squareup.anvil.annotations.ContributesTo

/**
 * Marker interface for object that can provide user scoped dependencies.
 */
@ContributesTo(AUserScope::class)
interface UserServices : Teardownable

/**
 * Marks as object as exposing a [TeardownHelper] that must be called before the reference
 * is nulled out.
 */
interface Teardownable {
    fun getTeardownHelper(): TeardownHelper
}

/**
 * Provide a method to teardown an object.
 */
interface TeardownHelper {
    /**
     * Tears down an object. Must be called just before the object becomes unreachable. After
     * this method is called, results for accessing the object are undefined.
     */
    fun teardown()
}
