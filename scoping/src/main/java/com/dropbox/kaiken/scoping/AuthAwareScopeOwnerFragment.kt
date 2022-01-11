package com.dropbox.kaiken.scoping

import androidx.fragment.app.FragmentActivity
import com.dropbox.kaiken.scoping.internal.AuthHelper
import com.dropbox.kaiken.scoping.internal.locateAuthHelperStore

/**
 * Fragment that starts a new user scope (as opposed to inheriting it from its parent activity).
 *
 * Unlike [AuthAwareFrargment] this interface starts a new user scope, this is useful when
 * the parent activity cannot implement an [AuthAwareScopeOwnerActivity] (like
 * [AuthRequiredActivity] or [AuthOptionalActivity]).
 *
 * Whenever possible you should avoid using this class directly and implement one of
 * [AuthOptionalFragment] or [AuthRequiredFragment].
 */
interface AuthAwareScopeOwnerFragment : DependencyProviderResolver {

    val authRequired: Boolean

    /**
     * Returns the parent activity of this fragment.
     *
     * Implementing class should avoid creating a custom implementation of this method, and instead
     * rely on the default implementation offered by [androidx.fragment.app.Fragment].
     */
    fun getActivity(): FragmentActivity?

    /**
     * Resolves the dependencies for the given type.
     *
     * [finishIfInvalidAuth] must be called before calling this method.
     */
    @JvmDefault
    override fun <T> resolveDependencyProvider(): T =
        locateAuthHelper().resolveDependencyProvider()

    /**
     * Finishes the parent activity of this fragment if the current authentication state is invalid
     * (e.g. a current viewing user id is presents but it's user services are not available),
     * otherwise it noops.
     *
     * This should be the first method that is called in `onAttach()` after the `super.onAttach()`
     * call.
     *
     * This method must be called before any calls to [resolveDependencyProvider].
     *
     * @return whether or not the activity has been finished.
     */
    @JvmDefault
    override fun finishIfInvalidAuth(): Boolean {
        val authHelper = locateAuthHelper()

        if (!authHelper.validateAuth()) {
            checkNotNull(getActivity()).finish()
            return true
        }

        return false
    }

    private fun locateAuthHelper(): AuthHelper =
        locateAuthHelperStore().authHelper
}
