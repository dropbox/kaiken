package com.dropbox.kaiken.processor

import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.processor.internal.GENERATED_BY_TOP_COMMENT
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

internal fun generateInjectorInterfaceFileSpec(
    pack: String,
    interfaceName: String,
    paramName: String,
    typeName: TypeName
): JavaFile {
    val interfaceSpec = generateInjectorInterface(interfaceName, paramName, typeName)

    return JavaFile.builder(pack, interfaceSpec)
        .addFileComment(GENERATED_BY_TOP_COMMENT)
        .build()
}

private fun generateInjectorInterface(
    interfaceName: String,
    paramName: String,
    typeName: TypeName
): TypeSpec {
    val interfaceBuilder = TypeSpec.interfaceBuilder(interfaceName)

    val injectorTypeName = ClassName.get(Injector::class.java)
    return interfaceBuilder
        .addSuperinterface(injectorTypeName)
        .addModifiers(Modifier.PUBLIC)
        .addMethod(
            MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                    ParameterSpec.builder(
                        typeName,
                        paramName
                    ).build()
                )
                .build()
        ).build()
}

internal fun resolveInjectorInterfaceName(
    typeElement: TypeElement
): String = "${typeElement.simpleName}Injector"
