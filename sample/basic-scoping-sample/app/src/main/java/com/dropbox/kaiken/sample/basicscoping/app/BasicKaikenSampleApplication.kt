package com.dropbox.kaiken.sample.basicscoping.app

import android.app.Application
import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.ScopedServicesProvider
import com.dropbox.kaiken.skeleton.scoping.UserServices

class BasicKaikenSampleApplication : Application(), ScopedServicesProvider {

    private lateinit var appServices: BasicKaikenSampleAppServices

    // Let's use a map to mimic our "UserManager".
    private val userServicesMap = mutableMapOf<String, BasicKaikenSampleUserServices>()

    override fun onCreate() {
        super.onCreate()

        appServices = BasicKaikenSampleAppServices()

        // Let's "log-in" in a user for sample purposes. In a real application this code would definitely not live
        // here and would be way way more complex
        val userProfile = UserProfile("awesome_user", "Awesome User")

        userServicesMap["awesome_user"] = BasicKaikenSampleUserServices(userProfile, appServices)
    }

    override fun provideAppServices(): AppServices = appServices

    override fun provideUserServicesOf(userId: String): UserServices? = userServicesMap.get(userId)
}
