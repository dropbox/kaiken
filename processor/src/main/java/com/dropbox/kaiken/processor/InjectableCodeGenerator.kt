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
import com.squareup.anvil.compiler.internal.requireFqName
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.callUtil.createLookupLocation
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import java.io.File

@ExperimentalStdlibApi
@ExperimentalAnvilApi
@AutoService(CodeGenerator::class)
class InjectableCodeGenerator : CodeGenerator {

    enum class ClassType {
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
                module.resolveClassByFqName(clazz.fqName!!, clazz.createLookupLocation()!!)
            descriptor!!.getAllSuperClassifiers().forEach {
                if (it.isFragment()) {
                    descriptor.validateFragment(clazz)
                    classType = ClassType.FRAGMENT
                } else if (it.isActivity()) {
                    descriptor.validateActivity(clazz)
                    classType = ClassType.ACTIVITY
                }
            }

            check(classType != ClassType.INVALID) {
                "Only Android Activities or Fragments can be annotated with" +
                    " ${Injectable::class.java.simpleName}"
            }
            val className = clazz.asClassName()
            val classFqName = clazz.requireFqName().toString()
            // val propertyName = classFqName.replace('.', '_')
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

    private fun ClassifierDescriptor.isFragment(): Boolean =
        this.getAllSuperClassifiers().any {
            FqName("androidx.fragment.app.Fragment") == it.fqNameSafe ||
                FqName("com.dropbox.kaiken.processor.internal.fakes.FakeFragment") == it.fqNameSafe
        }

    private fun ClassifierDescriptor.isActivity(): Boolean =
        this.getAllSuperClassifiers().any {
            FqName("androidx.appcompat.app.AppCompatActivity") == it.fqNameSafe ||
                FqName("com.dropbox.kaiken.processor.internal.fakes.FakeActivity") == it.fqNameSafe
        }

    private fun ClassifierDescriptor.implementsInjectorHolder(): Boolean =
        this.getAllSuperClassifiers().any {
            it.fqNameSafe == FqName("com.dropbox.kaiken.runtime.InjectorHolder")
        }

    private fun ClassDescriptor.validateFragment(clazz: KtClassOrObject) {
        check(this.visibility == DescriptorVisibilities.PUBLIC) {
            "The class ${clazz.fqName!!.shortName()} is not public"
        }

        check(!DescriptorUtils.classCanHaveAbstractDeclaration(this)) {
            "The class ${clazz.fqName!!.shortName()} is abstract"
        }

        check(this.isFragment()) {
            "The class ${clazz.fqName!!.shortName()} is not an Android activity. Found: ${
                this.getAllSuperClassifiers().toList().map { it.name }
            }"
        }
    }

    private fun ClassDescriptor.validateActivity(clazz: KtClassOrObject) {
        check(this.visibility == DescriptorVisibilities.PUBLIC) {
            "The class ${clazz.fqName!!.shortName()} is not public"
        }

        check(!DescriptorUtils.classCanHaveAbstractDeclaration(this)) {
            "The class ${clazz.fqName!!.shortName()} is abstract"
        }

        check(this.isActivity()) {
            "The class ${clazz.fqName!!.shortName()} is not an Android activity. Found: ${
                this.getAllSuperClassifiers().toList().map { it.name }
            }"
        }

        check(this.implementsInjectorHolder()) {
            "The class ${clazz.fqName!!.shortName()} does not implement" +
                " DependencyProviderResolver"
        }
    }
}
