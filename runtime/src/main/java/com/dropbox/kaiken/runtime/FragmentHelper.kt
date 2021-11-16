package com.dropbox.kaiken.runtime

import androidx.fragment.app.Fragment
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.annotation.InternalKaikenApi

@Suppress("UNCHECKED_CAST", "IfThenToElvis")
fun <InjectorType : Injector> Fragment.findInjector(): InjectorType {
    val injectorHolder = findInjectorHolder<InjectorType>()
        ?: throw InjectorNotFoundException(
            "Fragment ${this.javaClass} does not implement InjectorHolder" +
                " and neither its parent activity"
        )

    return injectorHolder.locateInjector()
}

@Suppress("UNCHECKED_CAST")
fun <InjectorType : Injector> Fragment.findInjectorHolder(): InjectorHolder<InjectorType>? {
    return when {
        this is InjectorHolder<*> -> {
            this as InjectorHolder<InjectorType>
        }
        activity is InjectorHolder<*> -> {
            activity as InjectorHolder<InjectorType>
        }
        else -> {
            KaikenRuntimeTestUtils.injectorHolderOverrideFor(this::class)
        }
    }
}
