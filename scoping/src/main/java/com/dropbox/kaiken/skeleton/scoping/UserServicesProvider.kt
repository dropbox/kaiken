package com.dropbox.kaiken.skeleton.scoping

/**
 * Interface for the user dependencies provider. This should be implemented by your [AppServices].
 */
interface UserServicesProvider {

    /**
     * Returns the user services of a given user or `null` if they are not available.
     *
     * The method MUST NOT throw if the services are not available.
     */
    fun provideUserServicesOf(userId: String): UserServices?

    /**
     * Returns the user services for a given viewing user selector or`null` if they are not
     * available.
     */
    @JvmDefault
    fun provideUserServicesOf(viewingUserSelector: ViewingUserSelector): UserServices? =
        provideUserServicesOf(viewingUserSelector.userId)
}
