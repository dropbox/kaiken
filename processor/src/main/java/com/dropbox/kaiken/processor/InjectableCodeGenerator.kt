package com.dropbox.kaiken.processor

import com.dropbox.common.inject.AuthOptionalScope
import com.dropbox.common.inject.AuthRequiredScope
import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.processor.internal.GENERATED_BY_TOP_COMMENT
import com.dropbox.kaiken.processor.internal.generateContributesInjector
import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.classesAndInnerClass
import com.squareup.anvil.compiler.internal.fqName
import com.squareup.anvil.compiler.internal.hasAnnotation
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.FileSpec
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
        }.flatMap(mapper(module, codeGenDir)).toList()
    }

    private fun mapper(
        module: ModuleDescriptor,
        codeGenDir: File
    ): (KtClassOrObject) -> List<GeneratedFile> =
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
                ) else
                generateFragmentFileSpec(
                    packageName,
                    "${className.simpleName}Injector",
                    className,
                    true
                )
            val injectorClasses = createGeneratedFile(
                codeGenDir = codeGenDir,
                packageName = packageName,
                fileName = "${className.simpleName}Injector",
                content = fileSpec.toString()
            )

            val authAwarenessScope: List<FqName>? = getAuthAwarenessScope(descriptor)

            val scopeClasses = authAwarenessScope?.mapNotNull { scopeFqName ->
                val contributor = generateContributesInjector(packageName, "${className.simpleName}Injector", scopeFqName) ?: return@mapNotNull null
                val fileBuilder = FileSpec.builder(packageName, "${className.simpleName}${scopeFqName.shortName().asString()}Contributor")
                val contributorFileSpec = fileBuilder.addComment(GENERATED_BY_TOP_COMMENT)
                    .addType(contributor)
                    .build()
                createGeneratedFile(
                    codeGenDir = codeGenDir,
                    packageName = packageName,
                    fileName = "${className.simpleName}${scopeFqName.shortName().asString()}Contributor",
                    content = contributorFileSpec.toString()
                )
            }

            val result = mutableListOf<GeneratedFile>()
            result.add(injectorClasses)
            if (scopeClasses != null) {
                result.addAll(scopeClasses)
            }
            result
        }

    override fun isApplicable(context: AnvilContext): Boolean = !context.disableComponentMerging

    @Suppress("UNCHECKED_CAST")
    private fun getAuthAwarenessScope(descriptor: ClassDescriptor): List<FqName>? {
        val customScope = descriptor.annotations.findAnnotation(FqName(INJECTABLE_FULLY_QUALIFIED_PATH))
            ?.allValueArguments
            ?.get(Name.identifier("scope"))
            ?.let {
                (it.value as ArrayList<KClassValue>)
                    .mapNotNull { scopeArrayValue -> scopeArrayValue.value as? KClassValue.Value.NormalClass }
                    .map { kClassValue -> kClassValue.classId.asSingleFqName() }
            }

        val interfaces = descriptor.getSuperInterfaces().joinToString(",")
        return when {
            customScope != null -> customScope
            interfaces.contains("AuthAware") -> listOf(AuthOptionalScope::class.fqName, AuthRequiredScope::class.fqName)
            interfaces.contains("AuthOptional") -> listOf(AuthOptionalScope::class.fqName, AuthRequiredScope::class.fqName)
            interfaces.contains("AuthRequired") -> listOf(AuthRequiredScope::class.fqName)
            else -> null
        }
    }

    companion object {
        private const val INJECTABLE_FULLY_QUALIFIED_PATH = "com.dropbox.kaiken.annotation.Injectable"
    }
}
