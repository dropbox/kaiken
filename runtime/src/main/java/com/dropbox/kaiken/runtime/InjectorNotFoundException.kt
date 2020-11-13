package com.dropbox.kaiken.runtime

/**
 * Thrown when a injector cannot be found for a given activity or fragment.
 *
 * This occurs when either
 * 1) An activity does not implement ActivityHolder OR
 * 2) A fragment does not implement and neither its parent activity
 */
class InjectorNotFoundException(message: String) : Exception(message)
