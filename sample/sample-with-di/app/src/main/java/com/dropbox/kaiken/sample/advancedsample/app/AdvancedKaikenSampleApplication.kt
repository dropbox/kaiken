package com.dropbox.kaiken.sample.advancedsample.app

import android.app.Application
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.ScopedServicesProvider
import com.dropbox.kaiken.scoping.UserServices

class AdvancedKaikenSampleApplication : Application(), ScopedServicesProvider {

    private lateinit var appServices: AdvancedKaikenSampleAppServices

    // Let's use a map to mimic our "UserManager".
    private val userServicesMap = mutableMapOf<String, AdvancedKaikenSampleUserServices>()

    override fun onCreate() {
        super.onCreate()

        appServices = DaggerAdvancedKaikenSampleAppServices.factory().create()

        // Let's "log-in" in a user for sample purposes. In a real application this code would definitely not live
        // here and would be way way more complex
        val userProfile = UserProfile("awesome_user", "Awesome User")

        userServicesMap["awesome_user"] = DaggerAdvancedKaikenSampleUserServices.factory().create(
            appServices,
            userProfile
        )
    }

    override fun provideAppServices(): AppServices = appServices

    override fun provideUserServicesOf(userId: String): UserServices? = userServicesMap.get(userId)
}
