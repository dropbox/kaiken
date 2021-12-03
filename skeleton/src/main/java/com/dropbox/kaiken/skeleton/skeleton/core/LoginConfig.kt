package com.dropbox.kaiken.skeleton.skeleton.core

data class LoginConfig(
        /**
         * Safety Net API to call Google with
         *
         * <pre>See <a href="https://dropbox-kms.atlassian.net/wiki/spaces/MF/
         * pages/445087901/Login+Setup+-+Android#Safety-Net-API-Key">Safety Net
         * API configuration</href> for more info.</pre>
         *
         * This is optional at the moment, but will be required once auth is integrated
         */
        val safetyNetApiKey: String = "defaultValue",
        /**
         * Google OAuth Client ID to use for Google Sign Up / Sign In's Server Side Access feature
         *
         * https://developers.google.com/identity/sign-in/android/offline-access
         *
         * <pre> See <a href="https://dropbox-kms.atlassian.net/wiki/spaces/MF/pages/445087901/
         * Login+Setup+-+Android#Google-Sign-In-%2F-Up-Setup">Google Sign In
         * Setup</href> for more info. </pre>
         *
         * This is optional at the moment, but will be required once auth is integrated
         */
        val googleServerClientId: String = "defaultValue",

        /**
         * Content to show in the Account Selection UI flow.
         *
         * See https://www.dropbox.com/scl/fi/3h5uwu580udyb9gw0rmb7/Mobile-Architecture-Review_-AccountMaker-Android.paper?dl=0&rlkey=aot2xcbr0vn8uneraw07kv9bi#:h2=Account-Selection
         * for additional details.
         *
         * This is optional at the moment, but will be required once auth is integrated.
         */
        val accountSelectionContent: AccountSelectionContent,

        /**
         * Url host used during Sign In With Apple.
         *
         * See [RealSignInWithAppleRespoistory.getSignInWithAppleRedirectUrl] for more context.
         * Each app needs to define its own host so there is no confusion which app tried to sign in
         * (and thus, which app should receive the call back). Recommended format: login-appname.
         *
         * This is optional at the moment, but will be required once auth is integrated.
         */
        val siaRedirectUrlScheme: String = "dbx-sia",

        /**
         * Scheme to be used when receiving a callback from the SSO login flow
         *
         * This will need to align with a backend configuration as well for SSO.
         *
         * See more details at https://dropbox-kms.atlassian.net/wiki/spaces/MF/pages/445087901/Login+Setup+-+Android#SSO,-MagicLink,-and-Sign-In-With-Apple-Callback-registration
         */
        val ssoScheme: String = "dbx-sso",
)
