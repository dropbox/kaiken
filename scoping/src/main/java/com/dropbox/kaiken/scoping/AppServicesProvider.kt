package com.dropbox.kaiken.scoping

/**
 * Interface for the app dependencies provider. This should be implemented by your application
 * class.
 */
interface AppServicesProvider {
    fun provideAppServices(): AppServices
}
