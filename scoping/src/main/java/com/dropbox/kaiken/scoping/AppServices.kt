package com.dropbox.kaiken.scoping

import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import javax.inject.Scope
import kotlin.reflect.KClass

interface AppServices : Teardownable


