package com.dropbox.kaiken.processor

import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.processor.internal.GENERATED_BY_TOP_COMMENT
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

internal fun generateInjectorInterfaceFileSpec(
    pack: String,
    interfaceName: String,
    paramName: String,
    targetType: TypeMirror,
    className: com.squareup.kotlinpoet.ClassName?
): FileSpec {
    val interfaceSpec =
        generateInjectorInterface(interfaceName, paramName, targetType, className)

    return FileSpec.builder(pack, "real$interfaceName")
        .addComment(GENERATED_BY_TOP_COMMENT)
        .addType(interfaceSpec)
        .build()
}

private fun generateInjectorInterface(
    interfaceName: String,
    paramName: String,
    targetType: TypeMirror,
    className: com.squareup.kotlinpoet.ClassName?
): com.squareup.kotlinpoet.TypeSpec {
    val interfaceBuilder = com.squareup.kotlinpoet.TypeSpec.interfaceBuilder(interfaceName)

    val injectorInterface = interfaceBuilder
        .addSuperinterface(Injector::class.java)
        .addFunction(
            FunSpec.builder("inject")
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(
                    com.squareup.kotlinpoet.ParameterSpec.builder(
                        "activity",
                        targetType.asTypeName()
                    )
                        .build()
                )
                .build()
        )

    if (anvilOnPath())
        injectorInterface.addAnnotation(
            com.squareup.kotlinpoet.AnnotationSpec.builder(
                com.squareup.kotlinpoet.ClassName(
                    "com.squareup.anvil.annotations",
                    "ContributesTo"
                )
            )
                .addMember(
                    "scope = %T::class",
                    className!!
                )
                .build()
        )
    return injectorInterface.build()
}

internal fun resolveInjectorInterfaceName(
    typeElement: TypeElement
): String = "${typeElement.simpleName}Injector"
