package com.dropbox.kaiken.skeleton.scoping

interface AppServices : Teardownable

/**
 * Marks as object as exposing a [TeardownHelper] that must be called before the reference
 * is nulled out.
 */
interface Teardownable {
    fun getTeardownHelper(): AppTeardownHelper
}

/**
 * Provide a method to teardown an object.
 */
interface AppTeardownHelper {
    /**
     * Tears down an object. Must be called just before the object becomes unreachable. After
     * this method is called, results for accessing the object are undefined.
     */
    fun teardown()
}
