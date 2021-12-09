package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.scoping.UserScope
import com.dropbox.kaiken.skeleton.scoping.UserServices
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(UserScope::class)
interface KaikenUserServices : UserServices
