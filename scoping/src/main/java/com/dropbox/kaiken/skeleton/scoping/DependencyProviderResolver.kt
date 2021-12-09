package com.dropbox.kaiken.skeleton.scoping

/**
 * Resolves the dependency provider of the given type in the user's [UserServices] or
 * [AppServices] depending on the current viewing user id.
 */
interface DependencyProviderResolver {
    fun finishIfInvalidAuth(): Boolean
    fun <T> resolveDependencyProvider(): T
}
