package com.dropbox.kaiken.processor

import com.dropbox.kaiken.annotation.Injectable
import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.reference.ClassReference
import com.squareup.anvil.compiler.internal.reference.asClassName
import com.squareup.anvil.compiler.internal.reference.classAndInnerClassReferences
import com.squareup.anvil.compiler.internal.safePackageString
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
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

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>,
    ): Collection<GeneratedFile> {
        return projectFiles.classAndInnerClassReferences(module).filter { clazz ->
            clazz.isAnnotatedWith(FqName("com.dropbox.kaiken.annotation.Injectable"))
        }.map(mapper(module, codeGenDir)).toList()
    }

    private fun mapper(
        module: ModuleDescriptor,
        codeGenDir: File
    ): (ClassReference.Psi) -> GeneratedFile =
        { psi: ClassReference.Psi ->
            var classType = ClassType.INVALID
            val descriptor: ClassDescriptor? =
                module.resolveClassByFqName(
                    psi.clazz.fqName!!,
                    checkNotNull(psi.clazz.createLookupLocation())
                )

            checkNotNull(descriptor).getAllSuperClassifiers().forEach {
                if (it.isFragment()) {
                    validateFragment(descriptor, psi.clazz)
                    classType = ClassType.FRAGMENT
                } else if (it.isAndroidActivity()) {
                    validateActivity(descriptor, psi.clazz)
                    classType = ClassType.ACTIVITY
                }
            }
            check(classType != ClassType.INVALID) {
                "Only Android Activities or Fragments can be annotated with" +
                    " ${Injectable::class.java.simpleName}"
            }
            val className = psi.asClassName()
            val packageName =
                (psi.clazz as KtClass).fqName!!.parent().safePackageString(dotPrefix = true)

            val fileSpec = if (classType == ClassType.ACTIVITY)
                generateActivityFileSpec(
                    packageName,
                    "${className.simpleName}Injector",
                    className
                ) else
                generateFragmentFileSpec(
                    packageName,
                    "${className.simpleName}Injector",
                    className,
                    true
                )
            createGeneratedFile(
                codeGenDir = codeGenDir,
                packageName = packageName,
                fileName = "${className.simpleName}Injector",
                content = fileSpec.toString()
            )
        }

    override fun isApplicable(context: AnvilContext): Boolean = !context.disableComponentMerging
}
