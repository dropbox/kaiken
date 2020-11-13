package com.dropbox.kaiken.scoping.internal

import android.content.Context
import com.dropbox.kaiken.scoping.ScopedServicesProvider

internal fun Context.locateScopedServicesProvider(): ScopedServicesProvider {
    return (this.applicationContext as ScopedServicesProvider)
}
