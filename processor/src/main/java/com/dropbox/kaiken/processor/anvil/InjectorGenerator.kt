package com.dropbox.kaiken.processor.anvil

import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.processor.internal.GENERATED_BY_TOP_COMMENT
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

internal class InjectorGenerator : CodeGenerator {
    override fun generateCode(
            codeGenDir: File,
            module: ModuleDescriptor,
            projectFiles: Collection<KtFile>
    ): Collection<CodeGenerator.GeneratedFile> {
        return projectFiles.asSequence()
                .flatMap { it.classesAndInnerClasses() }
                .filter { it.hasAnnotation(singletonAnnotation) }
                .map { clazz: KtClassOrObject ->
                    val packageName = clazz.containingKtFile.packageFqName.asString()
                    val className = clazz.requireFqName()
                            .asString()
                    val simpleName = clazz.name!!
                    val directory = File(codeGenDir, packageName.replace('.', File.separatorChar))
                    val file = File(directory, "Real"+simpleName+"Injector.kt")
                    check(file.parentFile.exists() || file.parentFile.mkdirs()) {
                        "Could not generate package directory: ${file.parentFile}"
                    }

//                    val scope = clazz.scope(contributesToFqName, module)

                    val content =  generateInjectorInterfaceFileSpec(packageName,
                            simpleName+"Injector",
                            className,
                            null
                    ).toString().trimIndent()

                    file.writeText(content)

                    CodeGenerator.GeneratedFile(file, content)
                }
                .toList()
    }
}

internal fun generateInjectorInterfaceFileSpec(
        pack: String,
        interfaceName: String,
        targetType: String,
        className: ClassName?
): FileSpec {
    val interfaceSpec =
            generateInjectorInterface(interfaceName, targetType, className)

    return FileSpec.builder(pack, "Real$interfaceName.kt")
            .addComment(GENERATED_BY_TOP_COMMENT)
            .addType(interfaceSpec)
            .build()
}

private fun generateInjectorInterface(
        interfaceName: String,
        targetType: String,
        className: ClassName?
): com.squareup.kotlinpoet.TypeSpec {
    val interfaceBuilder = com.squareup.kotlinpoet.TypeSpec.interfaceBuilder(interfaceName)

    val injectorInterface = interfaceBuilder
            .addSuperinterface(Injector::class.java)
            .addFunction(
                    FunSpec.builder("inject")
                            .addModifiers(KModifier.ABSTRACT)
//                            .addParameter(
//                                    com.squareup.kotlinpoet.ParameterSpec.builder(
//                                            "activity",
//                                            ClassName.bestGuess(targetType)
//                                    )
//                                            .build()
//                            )
                            .build()
            )

//    if (anvilOnPath())
//        injectorInterface.addAnnotation(
//                com.squareup.kotlinpoet.AnnotationSpec.builder(
//                        com.squareup.kotlinpoet.ClassName(
//                                "com.squareup.anvil.annotations",
//                                "ContributesTo"
//                        )
//                )
//                        .addMember(
//                                "scope = %T::class",
//                                className!!
//                        )
//                        .build()
//        )
    return injectorInterface.build()
}
