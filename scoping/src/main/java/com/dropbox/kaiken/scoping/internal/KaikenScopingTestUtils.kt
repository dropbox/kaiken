package com.dropbox.kaiken.scoping.internal

import com.dropbox.kaiken.annotation.InternalKaikenApi
import com.dropbox.kaiken.scoping.ViewingUserSelector

object KaikenScopingTestUtils {

    private var disableAuthValidation: Boolean = false
    private var resolveDependencyProviderOverride: Any? = null

    internal fun getAuthHelperStoreTestOverride(
        viewingUserSelector: ViewingUserSelector?
    ): AuthHelperStore? {
        if (disableAuthValidation) {
            return TestOverrideAuthHelperStore(
                TestOverrideAuthHelper(
                    viewingUserSelector,
                    resolveDependencyProviderOverride
                )
            )
        }
        return null
    }

    @InternalKaikenApi
    fun disableAuthValidation(resolveDependencyProviderOverride: Any?) {
        disableAuthValidation = true
        this.resolveDependencyProviderOverride = resolveDependencyProviderOverride
    }

    @InternalKaikenApi
    fun clearOverrides() {
        disableAuthValidation = false
        resolveDependencyProviderOverride = null
    }
}

private class TestOverrideAuthHelperStore(
    override val authHelper: TestOverrideAuthHelper
) : AuthHelperStore

private class TestOverrideAuthHelper(
    internal var viewingSelectorOverride: ViewingUserSelector?,
    private val resolveDependencyProviderOverride: Any?
) : AuthHelper {
    override val viewingUserSelector: ViewingUserSelector?
        get() = viewingSelectorOverride

    override fun validateAuth() = true

    @Suppress("UNCHECKED_CAST")
    override fun <T> resolveDependencyProvider(): T {
        requireNotNull(resolveDependencyProviderOverride) {
            "Disabling auth validation requires you to specify a resolveDependencyProviderOverride"
        }

        return resolveDependencyProviderOverride as T
    }
}
