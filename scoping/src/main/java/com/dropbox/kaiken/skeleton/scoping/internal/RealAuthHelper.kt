package com.dropbox.kaiken.skeleton.scoping.internal

import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.ScopedServicesProvider
import com.dropbox.kaiken.skeleton.scoping.UserServices
import com.dropbox.kaiken.skeleton.scoping.UserServicesProvider
import com.dropbox.kaiken.skeleton.scoping.ViewingUserSelector

/**
 * Servers as both as validator for the current authentication state, as as well as a dependency
 * provider resolver.
 *
 * Because the dependencies are resolved using application and user services, the authentication
 * state must be validated to guarantee that the correct dependencies are resolved. For example,
 * if a current viewing user is provided, then the dependencies MUST be resolved from the
 * [UserServices] of the corresponding user.
 *
 * There are scenarios in which the authentication would be deemed invalid:
 *
 * 1) We cannot retrieve the [UserServices] for the current viewing user (`viewingUserSelector`).
 * 2) Authorization is required (`authRequired`). But either a current viewing user is not
 * provided or we could not retrieve its [UserServices].
 *
 * @param scopedServicesProvider
 */
internal class RealAuthHelper(
    private val scopedServicesProvider: ScopedServicesProvider,
    override val viewingUserSelector: ViewingUserSelector?,
    private val authRequired: Boolean
) : AuthHelper {

    private val appServices: AppServices
        get() = scopedServicesProvider.provideAppServices()

    private val userServicesProvider: UserServicesProvider
        get() = scopedServicesProvider

    private var userServices: UserServices? = null

    private var validateAuthCalled = false

    /**
     * Whether or not the current authentication state is valid.
     *
     * It is possible for this method to be called multiple times. This allows for this object
     * to be retained as part of a lifecycle.
     *
     * This must be called before [resolveDependencyProvider].
     */
    @Synchronized
    override fun validateAuth(): Boolean {
        if (!validateAuthCalled) {
            validateAuthCalled = true
            userServices = locateUserServices()
        }

        return validAuth()
    }

    private fun locateUserServices(): UserServices? {
        require(validateAuthCalled)

        val viewingUserId = viewingUserSelector?.userId

        return if (viewingUserId == null) {
            null
        } else {
            userServicesProvider.provideUserServicesOf(viewingUserId)
        }
    }

    private fun validAuth() = !needUserServices() || (userServices != null)

    @Synchronized
    private fun needUserServices(): Boolean {
        val userIdProvided = (viewingUserSelector?.userId != null)

        return authRequired || userIdProvided
    }

    /**
     * Returns the dependencies of the given type.
     *
     * @throws IllegalStateException if [validateAuth] is not called first.
     */
    @Suppress("UNCHECKED_CAST")
    @Synchronized
    override fun <T> resolveDependencyProvider(): T {
        check(validateAuthCalled) {
            "finishIfInvalidAuth() must be called before calling resolveDependencyProvider()"
        }

        check(validAuth())

        return if (userServices != null) {
            (userServices as T)
        } else {
            check(!authRequired)
            (appServices as T)
        }
    }
}
