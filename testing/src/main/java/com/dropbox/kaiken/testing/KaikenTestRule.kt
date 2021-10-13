package com.dropbox.kaiken.testing

import androidx.fragment.app.Fragment
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.annotation.InternalKaikenApi
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.KaikenRuntimeTestUtils
import com.dropbox.kaiken.scoping.AuthAwareFragment
import com.dropbox.kaiken.scoping.ScopedServicesProvider
import com.dropbox.kaiken.scoping.internal.KaikenScopingTestUtils
import org.junit.rules.ExternalResource
import kotlin.reflect.KClass

class KaikenTestRule(
    /**
     * Makes calls to [DependencyProviderResolver.finishIfInvalidAuth] succeed even if the
     * the authentication state is invalid. Normal auth state validation requires the application
     * context to implement [ScopedServicesProvider] which is doable but cumbersome in tests.
     *
     * Because this is short-circuits the dependency provider resolution, you'll need to provider
     * your own dependency provider either by specifying a `resolveDependencyProviderOverride`,
     * an injector factory override or an injector override.
     *
     * Note regarding [AuthAwareFragment]s:
     * The dependency resolution for these will still go through their parent fragment
     * or parent activity. So when testing an [AuthAwareFragment], it should still be a child
     * of a scoping owning fragment or activity, otherwise an exception will be thrown.
     */
    private val disableAuthValidation: Boolean = true,

    /**
     * Overrides the dependencies that should be returned when calling
     * [DependencyProviderResolver.resolveDependencyProvider].
     *
     * Note regarding [AuthAwareFragment]s:
     * The dependency resolution for these will still go through their parent fragment
     * or parent activity. So when testing an [AuthAwareFragment], it should still be a child
     * of a scoping owning fragment or activity, otherwise an exception will be thrown.
     */
    private val resolveDependencyProviderOverride: Any? = null,

    /**
     * For the given [InjectorHolder]s, bypasses the call to [getInjectorFactory] an instead
     * returns the given override.
     */
    private val injectorFactoryOverrides: Map<KClass<out InjectorHolder<*>>, InjectorFactory<*>> = mapOf(),

    /**
     * For the given [AuthAwareFragment], short-circuits the entire dependency resolution and
     * injector creation and instead uses the given [Injector].
     *
     * Use this carefully. Whenever possible try to make the fragment a child of an injector holder
     * and override the injector factory for the parent instead. This is to try to keep the
     * dependency resolution mechanism as possible to production as possible.
     */
    private val injectorOverride: Pair<KClass<out AuthAwareFragment>, Injector>? = null,

) : ExternalResource() {

    @OptIn(InternalKaikenApi::class)
    override fun before() {
        KaikenRuntimeTestUtils.testMode = true
        KaikenRuntimeTestUtils.clearOverrides()

        if (disableAuthValidation) {
            KaikenScopingTestUtils.disableAuthValidation(resolveDependencyProviderOverride)
        }

        injectorFactoryOverrides.iterator().forEach { (key, value) ->
            KaikenRuntimeTestUtils.addInjectorFactoryOverride(key, value)
        }

        if (injectorOverride != null) {
            @Suppress("UNCHECKED_CAST")
            KaikenRuntimeTestUtils.addInjectorOverride(
                injectorOverride.first as KClass<out Fragment>,
                injectorOverride.second
            )
        }
    }

    @OptIn(InternalKaikenApi::class)
    override fun after() {
        KaikenRuntimeTestUtils.clearOverrides()
        KaikenScopingTestUtils.clearOverrides()
        KaikenRuntimeTestUtils.testMode = false
    }
}
