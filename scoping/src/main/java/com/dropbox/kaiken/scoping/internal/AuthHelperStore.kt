package com.dropbox.kaiken.scoping.internal

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dropbox.kaiken.scoping.AuthAwareBroadcastReceiver
import com.dropbox.kaiken.scoping.AuthAwareScopeOwnerActivity
import com.dropbox.kaiken.scoping.AuthAwareScopeOwnerFragment
import com.dropbox.kaiken.scoping.BuildConfig
import com.dropbox.kaiken.scoping.ScopedServicesProvider
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.getViewingUserSelector

internal fun AuthAwareScopeOwnerActivity.locateAuthHelperStore(): AuthHelperStore {
    val activity = (this as ComponentActivity)
    return activity.locateAuthHelperStore(authRequired)
}

internal fun AuthAwareScopeOwnerFragment.locateAuthHelperStore(): AuthHelperStore {
    val fragment = (this as Fragment)
    return fragment.locateAuthHelperStore(authRequired)
}

fun AuthAwareBroadcastReceiver.locateScopedServicesProvider(
    context: Context
): ScopedServicesProvider {
    return context.locateScopedServicesProvider()
}

private fun ComponentActivity.locateAuthHelperStore(authRequired: Boolean): AuthHelperStore {
    val viewingUserSelector = intent.getViewingUserSelector()

    if (BuildConfig.DEBUG) {
        KaikenScopingTestUtils.getAuthHelperStoreTestOverride(viewingUserSelector)?.let { override ->
            return override
        }
    }

    val appServicesProvider = locateScopedServicesProvider()

    return locateAuthHelperStore(appServicesProvider, viewingUserSelector, authRequired)
}

private fun Fragment.locateAuthHelperStore(authRequired: Boolean): AuthHelperStore {
    val viewingUserSelector = arguments?.getViewingUserSelector()

    if (BuildConfig.DEBUG) {
        KaikenScopingTestUtils.getAuthHelperStoreTestOverride(viewingUserSelector)?.let { override ->
            return override
        }
    }

    val context = requireActivity()
    val scopedServicesProvider = context.locateScopedServicesProvider()

    return locateAuthHelperStore(scopedServicesProvider, viewingUserSelector, authRequired)
}

private fun ViewModelStoreOwner.locateAuthHelperStore(
    scopedServicesProvider: ScopedServicesProvider,
    viewingUserSelector: ViewingUserSelector?,
    authRequired: Boolean
): AuthHelperStore {

    val viewModelProvider = ViewModelProvider(
        this,
        AuthViewModelFactory(scopedServicesProvider, viewingUserSelector, authRequired)
    )

    return viewModelProvider.get(AuthViewModel::class.java)
}

@Suppress("UNCHECKED_CAST")
internal class AuthViewModelFactory(
    private val scopedServicesProvider: ScopedServicesProvider,
    private val viewingUserSelector: ViewingUserSelector?,
    private val authRequired: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(
            RealAuthHelper(
                scopedServicesProvider, viewingUserSelector, authRequired
            )
        ) as T
    }
}

internal interface AuthHelperStore {
    val authHelper: AuthHelper
}

internal class AuthViewModel(
    override val authHelper: AuthHelper
) : ViewModel(), AuthHelperStore
