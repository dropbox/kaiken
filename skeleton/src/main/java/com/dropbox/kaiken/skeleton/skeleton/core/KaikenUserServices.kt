package com.dropbox.kaiken.skeleton.skeleton.core

import com.dropbox.kaiken.scoping.UserScope
import com.dropbox.kaiken.scoping.UserServices
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(UserScope::class)
interface KaikenUserServices : UserServices
