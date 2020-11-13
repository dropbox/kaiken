package com.dropbox.kaiken.scoping

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import com.dropbox.kaiken.scoping.internal.AuthHelper
import com.dropbox.kaiken.scoping.internal.locateAuthHelperStore

/**
 * An activity that is authentication aware (i.e. the activity is running under a specific user
 * scope which is indicated by its associated viewing user selector).
 *
 * This interface relies on the implementor extending from [androidx.fragment.app.FragmentActivity].
 */
interface AuthAwareScopeOwnerActivity : DependencyProviderResolver, ViewModelStoreOwner {

    val authRequired: Boolean

    @JvmDefault
    fun getViewingUserSelector(): ViewingUserSelector? =
        locateAuthHelper().viewingUserSelector

    /**
     * Resolves the dependencies for the given type.
     *
     * [finishIfInvalidAuth] must be called before calling this method.
     */
    @JvmDefault
    override fun <T> resolveDependencyProvider(): T =
        locateAuthHelper().resolveDependencyProvider()

    /**
     * Finishes the activity if the current authentication state is invalid (e.g. a current
     * viewing user id is presents but it's user services are not available), otherwise it noops.
     *
     * This should be the first method that is called `onCreate()` after the `super.onCreate()`
     * call.
     *
     * This method must be called before any calls to [resolveDependencyProvider].
     *
     * @return whether or not the activity has been finished.
     */
    @JvmDefault
    override fun finishIfInvalidAuth(): Boolean {
        val activity = (this as FragmentActivity)
        val authHelper = locateAuthHelper()

        if (!authHelper.validateAuth()) {
            activity.finish()
            return true
        }

        return false
    }

    private fun locateAuthHelper(): AuthHelper =
        locateAuthHelperStore().authHelper
}
