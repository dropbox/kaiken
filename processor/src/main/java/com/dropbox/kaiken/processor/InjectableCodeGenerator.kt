package com.dropbox.kaiken.processor

import com.dropbox.kaiken.annotation.Furiex
import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.classesAndInnerClass
import com.squareup.anvil.compiler.internal.hasAnnotation
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.javapoet.JClassName
import com.squareup.kotlinpoet.javapoet.JTypeName
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toJTypeName
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
import org.jetbrains.kotlin.resolve.jvm.JvmClassName
import org.jetbrains.kotlin.resolve.jvm.JvmClassName.byClassId
import java.io.File
import kotlin.reflect.KClass

@ExperimentalAnvilApi
@AutoService(CodeGenerator::class)
class InjectableCodeGenerator : CodeGenerator {

    @KotlinPoetJavaPoetPreview
    override fun generateCode(codeGenDir: File, module: ModuleDescriptor, projectFiles: Collection<KtFile>): Collection<GeneratedFile> {
        return projectFiles.classesAndInnerClass(module).filter { clazz ->
            // TODO(changd): replace with Injectable
            clazz.hasAnnotation(FqName("com.dropbox.kaiken.annotation.Furiex"), module)
        }.map { clazz: KtClassOrObject ->
            var foundActivityOrFragment = false
            val descriptor: ClassDescriptor? = module.resolveClassByFqName(clazz.fqName!!, clazz.createLookupLocation()!!)
            descriptor!!.getAllSuperClassifiers().forEach {
                if (it.isFragment()) {
                    descriptor.validateFragment(clazz)
                    foundActivityOrFragment = true
                } else if (it.isActivity()) {
                    descriptor.validateActivity(clazz)
                    foundActivityOrFragment = true
                }
            }

            check(foundActivityOrFragment) {
                "Only Android Activities or Fragments can be annotated with" +
                    " ${Furiex::class.java.simpleName}"
            }
            val className = clazz.asClassName()
            val packageName: FqName = (clazz as KtClass).fqName!!.parent()

            val interfaceFileSpec = generateInjectorInterfaceFileSpec(packageName.asString(), "${className.simpleName}Injector", "activity", className.toJTypeName())

            createGeneratedFile(
                codeGenDir = codeGenDir,
                packageName = packageName.asString(),
                fileName = "TestFile",
                content = """
                    package ${packageName.asString()}
                    
                    interface TestFile
                    """
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
//        check(this.visibility == DescriptorVisibilities.PUBLIC) {
//            "The class ${clazz.fqName!!.shortName()} is not public"
//        }
//
//        check(!DescriptorUtils.classCanHaveAbstractDeclaration(this)) {
//            "The class ${clazz.fqName!!.shortName()} is abstract"
//        }
//
//        check(this.isFragment()) {
//            "The class ${clazz.fqName!!.shortName()} is not an Android activity. Found: ${this.getAllSuperClassifiers().toList().map { it.name }}"
//        }
    }

    private fun ClassDescriptor.validateActivity(clazz: KtClassOrObject) {
//        check(this.visibility == DescriptorVisibilities.PUBLIC) {
//            "The class ${clazz.fqName!!.shortName()} is not public"
//        }
//
//        check(!DescriptorUtils.classCanHaveAbstractDeclaration(this)) {
//            "The class ${clazz.fqName!!.shortName()} is abstract"
//        }
//
//        check(this.isActivity()) {
//            "The class ${clazz.fqName!!.shortName()} is not an Android activity. Found: ${this.getAllSuperClassifiers().toList().map { it.name }}"
//        }
//
//        check(this.implementsInjectorHolder()) {
//            "The class ${clazz.fqName!!.shortName()} does not implement" +
//                    " DependencyProviderResolver"
//        }
    }
}
