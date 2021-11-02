package com.dropbox.kaiken.processor

import com.dropbox.kaiken.annotation.Injectable
import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.classesAndInnerClass
import com.squareup.anvil.compiler.internal.hasAnnotation
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.calls.callUtil.createLookupLocation
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import java.io.File

@ExperimentalStdlibApi
@ExperimentalAnvilApi
@AutoService(CodeGenerator::class)
class InjectableCodeGenerator : CodeGenerator {

    internal enum class ClassType {
        ACTIVITY, FRAGMENT, INVALID
    }

    @KotlinPoetJavaPoetPreview
    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFile> {
        return projectFiles.classesAndInnerClass(module).filter { clazz ->
            // TODO(changd): replace with Injectable
            clazz.hasAnnotation(FqName("com.dropbox.kaiken.annotation.Injectable"), module)
        }.map { clazz: KtClassOrObject ->
            var classType = ClassType.INVALID
            val descriptor: ClassDescriptor? =
                module.resolveClassByFqName(clazz.fqName!!, checkNotNull(clazz.createLookupLocation()))

            checkNotNull(descriptor).getAllSuperClassifiers().forEach {
                if (it.isFragment()) {
                    validateFragment(descriptor, clazz)
                    classType = ClassType.FRAGMENT
                } else if (it.isActivity()) {
                    validateActivity(descriptor, clazz)
                    classType = ClassType.ACTIVITY
                }
            }
            check(classType != ClassType.INVALID) {
                "Only Android Activities or Fragments can be annotated with" +
                    " ${Injectable::class.java.simpleName}"
            }
            val className = clazz.asClassName()
            val packageName =
                (clazz as KtClass).fqName!!.parent().safePackageString(dotPrefix = true)

            val fileSpec = if (classType == ClassType.ACTIVITY)
                generateActivityFileSpec(
                    packageName,
                    "${className.simpleName}Injector",
                    className
                ) else
                generateFragmentFileSpec(
                    packageName,
                    "${className.simpleName}Injector",
                    className
                )
            return@map createGeneratedFile(
                codeGenDir = codeGenDir,
                packageName = packageName,
                fileName = "${className.simpleName}Injector",
                content = fileSpec.toString()
            )
        }.toList()
    }

    override fun isApplicable(context: AnvilContext): Boolean = !context.disableComponentMerging
}
