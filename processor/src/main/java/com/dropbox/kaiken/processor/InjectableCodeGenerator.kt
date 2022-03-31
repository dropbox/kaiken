package com.dropbox.kaiken.processor

import com.dropbox.common.inject.AuthOptionalScope
import com.dropbox.common.inject.AuthRequiredScope
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
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.calls.callUtil.createLookupLocation
import org.jetbrains.kotlin.resolve.constants.KClassValue
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import java.io.File
import kotlin.reflect.KClass

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
        projectFiles: Collection<KtFile>,
    ): Collection<GeneratedFile> {
        return projectFiles.classesAndInnerClass(module).filter { clazz ->
            clazz.hasAnnotation(FqName(INJECTABLE_FULLY_QUALIFIED_PATH), module)
        }.map(mapper(module, codeGenDir)).toList()
    }

    private fun mapper(
        module: ModuleDescriptor,
        codeGenDir: File
    ): (KtClassOrObject) -> GeneratedFile =
        { clazz: KtClassOrObject ->
            var classType = ClassType.INVALID
            val descriptor: ClassDescriptor? =
                module.resolveClassByFqName(
                    clazz.fqName!!,
                    checkNotNull(clazz.createLookupLocation())
                )

            checkNotNull(descriptor).getAllSuperClassifiers().forEach {
                if (it.isFragment()) {
                    validateFragment(descriptor, clazz)
                    classType = ClassType.FRAGMENT
                } else if (it.isAndroidActivity()) {
                    validateActivity(descriptor, clazz)
                    classType = ClassType.ACTIVITY
                }
            }

            val authAwarenessScope: KClass<*>? = getAuthAwarenessScope(descriptor)

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
                    className,
                    authAwarenessScope
                ) else
                generateFragmentFileSpec(
                    packageName,
                    "${className.simpleName}Injector",
                    className,
                    authAwarenessScope,
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

    @Suppress("UNCHECKED_CAST")
    private fun getAuthAwarenessScope(descriptor: ClassDescriptor): KClass<*>? {
        val customScope = descriptor.annotations.findAnnotation(FqName(INJECTABLE_FULLY_QUALIFIED_PATH))
            ?.allValueArguments
            ?.get(Name.identifier("scope"))
            ?.let {
                val kClassValue = (it.value as ArrayList<KClassValue>).firstOrNull()?.value as? KClassValue.Value.NormalClass
                    ?: return null
                Class.forName(kClassValue.classId.asSingleFqName().asString()).kotlin
            }

        val interfaces = descriptor.getSuperInterfaces().joinToString(",")
        return when {
            customScope != null -> customScope
            interfaces.contains("AuthOptional") -> AuthOptionalScope::class
            interfaces.contains("AuthRequired") -> AuthRequiredScope::class
            else -> null
        }
    }

    companion object {
        private const val INJECTABLE_FULLY_QUALIFIED_PATH = "com.dropbox.kaiken.annotation.Injectable"
    }
}
