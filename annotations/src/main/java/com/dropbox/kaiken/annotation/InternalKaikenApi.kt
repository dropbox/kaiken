package com.dropbox.kaiken.annotation

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Kaiken internal api methods to expose hooks to test libraries. Do not use!"
)
annotation class InternalKaikenApi
