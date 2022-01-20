package com.dropbox.kaiken.skeleton.core

import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.scoping.UserServices
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(UserScope::class)
interface KaikenUserServices : UserServices
