package com.dropbox.kaiken.scoping

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * An fragment that runs under the user scope of the parent activity.
 *
 * The parent activity of this fragment must be an [AuthAwareScopeOwnerActivity] (most likely either
 * an [AuthOptionalActivity] or an [AuthRequiredActivity]).
 *
 * If you need to start a new user scope or your parent activity does not implement
 * [AuthAwareScopeOwnerActivity] then you'll need to implement [AuthOptionalFragment] or
 * [AuthRequiredFragment] instead.
 *
 * This classes should be implemented by children of [androidx.fragment.app.Fragment], which will
 * provide the implementation of [getActivity].
 */
interface AuthAwareFragment : DependencyProviderResolver {

    /**
     * Returns the parent activity of this fragment.
     *
     * Implementing class should avoid creating a custom implementation of this method, and instead
     * rely on the default implementation offered by [androidx.fragment.app.Fragment].
     */
    fun getActivity(): FragmentActivity?

    /**
     * Returns the parent fragment of this fragment if any.
     *
     * Implementing class should avoid creating a custom implementation of this method, and instead
     * rely on the default implementation offered by [androidx.fragment.app.Fragment].
     */
    fun getParentFragment(): Fragment?

    /**
     * Implementation of [getViewingUserSelector] that fetches it from its parent activity.
     *
     * Parent activity most implement [AuthAwareScopeOwnerActivity].
     *
     * @throws IllegalArgumentException if the parent activity is not [AuthAwareScopeOwnerActivity].
     */
    fun getViewingUserSelector(): ViewingUserSelector? =
        requireAuthAwareActivity().getViewingUserSelector()

    /**
     * Finishes the parent activity of this fragment if the current authentication state is invalid
     * (e.g. a current viewing user id is presents but it's user services are not available),
     * otherwise it noops.
     *
     * It validates auth via its parent fragment if any or from its parent activity.
     *
     * This should be the first method that is called in `onAttach()` after the `super.onAttach()`
     * call.
     *
     * This method must be called before any calls to [resolveDependencyProvider].
     *
     * @return whether or not the activity has been finished.
     */
    override fun finishIfInvalidAuth(): Boolean {
        val parentFragment = parentFragmentAsDependencyResolver()

        if (parentFragment != null) {
            return parentFragment.finishIfInvalidAuth()
        }

        return requireAuthAwareActivity().finishIfInvalidAuth()
    }

    /**
     * Implementation of [resolveDependencyProvider] that fetches the dependencies from its
     * parent fragment if any or from its parent activity.
     */
    override fun <T> resolveDependencyProvider(): T {
        val parentFragment = parentFragmentAsDependencyResolver()

        if (parentFragment != null) {
            return parentFragment.resolveDependencyProvider()
        }

        return requireAuthAwareActivity().resolveDependencyProvider()
    }

    private fun parentFragmentAsDependencyResolver(): DependencyProviderResolver? {
        val parentFragment = getParentFragment()

        if (parentFragment != null) {
            require(parentFragment is DependencyProviderResolver) {
                "AuthAwareFragment parent must implement AuthAwareFragment " +
                    "or other dependency resolver"
            }

            return parentFragment
        }

        return parentFragment
    }

    private fun requireAuthAwareActivity(): AuthAwareScopeOwnerActivity {
        val activity = getActivity()

        require(activity is AuthAwareScopeOwnerActivity) {
            "AuthAwareFragment must be attached to an activity that implements AuthAwareActivity"
        }

        return activity
    }
}
