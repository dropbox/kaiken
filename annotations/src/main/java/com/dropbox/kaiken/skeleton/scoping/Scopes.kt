package com.dropbox.kaiken.skeleton.scoping

import javax.inject.Scope
import kotlin.reflect.KClass

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleIn(val clazz: KClass<*>)

abstract class SkeletonScope private constructor()
abstract class AppScope private constructor()
abstract class UserScope private constructor()
abstract class AuthRequiredScope private constructor()
abstract class AuthOptionalScope private constructor()
abstract class AuthOptionalScreenScope private constructor()
abstract class AuthRequiredScreenScope private constructor()