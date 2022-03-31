package com.dropbox.kaiken.processor.internal

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass

internal fun generateContributesInjector(
    pack: String,
    interfaceName: String,
    authAwarenessScope: KClass<out Any>?
): TypeSpec? {
    if (authAwarenessScope == null) {
        return null
    }
    val interfaceBuilder = TypeSpec.interfaceBuilder("${interfaceName}ScopeContributor")
    val contributionAnnotationScope = MemberName("com.dropbox.common.inject", "${authAwarenessScope.simpleName}")
    return interfaceBuilder
        .addAnnotation(
            AnnotationSpec.builder(ContributesTo::class)
                .addMember("scope=%M::class", contributionAnnotationScope)
                .build()
        )
        .addSuperinterface(ClassName(pack, interfaceName))
        .addModifiers(KModifier.PUBLIC)
        .build()
}