package com.dropbox.kaiken.skeleton.skeleton.core

import java.util.Locale

data class AppInfoProvider(
    val appKey: String,
    val appSecret: String,
    val locale: Locale,
    /**
     * Client identifier for HTTP requests.
     */
    val clientIdentifier: String,
    val loginConfig: LoginConfig
)

data class AccountSelectionContent(val title: Int, val description: Int, val appIcon: Int)
