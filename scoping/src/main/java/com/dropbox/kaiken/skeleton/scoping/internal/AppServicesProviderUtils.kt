package com.dropbox.kaiken.skeleton.scoping.internal

import android.content.Context
import com.dropbox.kaiken.skeleton.scoping.ScopedServicesProvider

internal fun Context.locateScopedServicesProvider(): ScopedServicesProvider {
    return (this.applicationContext as ScopedServicesProvider)
}
