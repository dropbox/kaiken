package com.dropbox.kaiken.skeleton.scoping.internal

import com.dropbox.kaiken.skeleton.scoping.ViewingUserSelector

internal interface AuthHelper {

    val viewingUserSelector: ViewingUserSelector?

    /**
     * Whether or not the current authentication state is valid.
     *
     * It is possible for this method to be called multiple times. This allows for this object
     * to be retained as part of a lifecycle.
     *
     * This must be called before [resolveDependencyProvider].
     */
    fun validateAuth(): Boolean

    /**
     * Returns the dependencies of the given type.
     *
     * @throws IllegalStateException if [validateAuth] is not called first.
     */
    fun <T> resolveDependencyProvider(): T
}
