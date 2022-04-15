package com.dropbox.kaiken.processor.internal

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec
import org.jetbrains.kotlin.name.FqName

internal fun generateContributesInjector(
    pack: String,
    interfaceName: String,
    authAwarenessScope: FqName
): TypeSpec {
    val interfaceBuilder = TypeSpec.interfaceBuilder("${interfaceName}${authAwarenessScope.shortName().asString()}Contributor")
    val contributionAnnotationScope = MemberName(authAwarenessScope.parent().asString(), authAwarenessScope.shortName().asString())
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
