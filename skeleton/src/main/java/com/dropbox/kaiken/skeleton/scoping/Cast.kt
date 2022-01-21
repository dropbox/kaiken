package com.dropbox.kaiken.skeleton.scoping

inline fun <reified T> Any.cast(): T = this as T
