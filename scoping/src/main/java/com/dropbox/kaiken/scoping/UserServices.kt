package com.dropbox.kaiken.scoping

/**
 * Marker interface for object that can provide user scoped dependencies.
 */
interface UserServices : UserTeardownable


/**
 * Marks as object as exposing a [TeardownHelper] that must be called before the reference
 * is nulled out.
 */
interface UserTeardownable {
    fun getUserTeardownHelper(): UserTeardownHelper
}


/**
 * Provide a method to teardown an object.
 */
interface UserTeardownHelper {
    /**
     * Tears down an object. Must be called just before the object becomes unreachable. After
     * this method is called, results for accessing the object are undefined.
     */
    fun teardown()
}
