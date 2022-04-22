package com.dropbox.kaiken.processor

import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.processor.internal.GENERATED_BY_TOP_COMMENT
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.classesAndInnerClass
import com.squareup.anvil.compiler.internal.decapitalize
import com.squareup.anvil.compiler.internal.hasAnnotation
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.calls.callUtil.createLookupLocation
import org.jetbrains.kotlin.resolve.constants.KClassValue
import java.io.File

/**
 * Adds a generator to help defining custom scopes to use in kaiken. It is used when a class is
 * annotated with [CustomScope]. This helps simplify creating new scopes by generating most
 * boilerplate required for their usage
 *
 * <pre>
 *     @CustomScope(parentScope = UserScope::class)
 *     abstract class MyCustomScope private constructor()
 * </pre>
 *
 * It generates two items
 * 1. A subcomponent
 * 2. An InjectorFactory
 *
 * <pre>
 *    @ContributesSubcomponent(
 *      scope=MyCustomScope::class,
 *      parentScope=UserScope::class
 *    )
 *    @SingleIn(MyCustomScope::class)
 *    public interface MyCustomScopeComponent : Injector {
 *      @ContributesTo(UserScope::class)
 *      public interface ParentComponent : Injector {
 *          public fun createMyCustomScopeComponent(): MyCustomScopeComponent
 *      }
 *    }
 *    public inline fun <reified T : Injector> DependencyProviderResolver.myCustomScopeInjectorFactory() =
 *      InjectorFactory { (resolveDependencyProvider() as
 *      MyCustomScopeComponent.ParentComponent).createMyCustomScopeComponent() as T }
 * </pre>
 */
@ExperimentalStdlibApi
@ExperimentalAnvilApi
@AutoService(CodeGenerator::class)
class CustomScopeGenerator : CodeGenerator {
    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFile> = projectFiles.classesAndInnerClass(module).filter { clazz ->
        clazz.hasAnnotation(FqName(CUSTOM_SCOPE_FULLY_QUALIFIED_PATH), module)
    }.flatMap(mapper(module, codeGenDir)).toList()

    private fun mapper(
        module: ModuleDescriptor,
        codeGenDir: File
    ): (KtClassOrObject) -> List<GeneratedFile> = { clazz: KtClassOrObject ->
        val descriptor: ClassDescriptor? =
            module.resolveClassByFqName(
                clazz.fqName!!,
                checkNotNull(clazz.createLookupLocation())
            )
        val className = clazz.asClassName()
        val packageName =
            (clazz as KtClass).fqName!!.parent().safePackageString(dotPrefix = true)

        val constructors = descriptor?.constructors ?: error("Must have at least one constructor")

        if (constructors.size > 1 || constructors.first()?.visibility != DescriptorVisibilities.PRIVATE) {
            error("Custom scope must have a single private constructor")
        }

        // figure out a way to check if abstract?

        val parentScope = descriptor.annotations.findAnnotation(FqName(CUSTOM_SCOPE_FULLY_QUALIFIED_PATH))
            ?.allValueArguments
            ?.get(Name.identifier("parentScope"))
            ?.let {
                val normalClass = it.value as? KClassValue.Value.NormalClass
                normalClass?.classId?.asSingleFqName()
            } ?: error("Must define parent scope in annotation")

        val subcomponent = generateSubcomponent(clazz.fqName!!, parentScope)
        val injectorFactory = generateInjectorFactory(clazz.fqName!!)

        val fileBuilder = FileSpec.builder(packageName, "${className.simpleName}Helpers")
        val contributorFileSpec = fileBuilder.addComment(GENERATED_BY_TOP_COMMENT)
            .addType(subcomponent)
            .addFunction(injectorFactory)
            .build()

        val file = createGeneratedFile(
            codeGenDir = codeGenDir,
            packageName = packageName,
            fileName = "${className.simpleName}Helpers",
            content = contributorFileSpec.toString()
        )

        listOf(file)
    }

    private fun generateSubcomponent(scopeName: FqName, parentScope: FqName): TypeSpec {
        val interfaceBuilder = TypeSpec.interfaceBuilder("${scopeName.shortName().asString()}Component")
        val newScopeMemberName = MemberName(scopeName.parent().asString(), scopeName.shortName().asString())
        val parentScopeMemberName = MemberName(parentScope.parent().asString(), parentScope.shortName().asString())
        return interfaceBuilder
            .addAnnotation(
                AnnotationSpec.builder(ContributesSubcomponent::class)
                    .addMember("scope=%M::class", newScopeMemberName)
                    .addMember("parentScope=%M::class", parentScopeMemberName)
                    .build()
            )
            .addAnnotation(
                AnnotationSpec.builder(SingleIn::class)
                    .addMember("%M::class", newScopeMemberName)
                    .build()
            )
            .addType(
                TypeSpec.interfaceBuilder("ParentComponent")
                    .addAnnotation(
                        AnnotationSpec.builder(ContributesTo::class)
                            .addMember("%M::class", parentScopeMemberName)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("create${scopeName.shortName().asString()}Component")
                            .addModifiers(KModifier.ABSTRACT)
                            .returns(ClassName(scopeName.parent().asString(), "${scopeName.shortName().asString()}Component"))
                            .build()
                    )
                    .addSuperinterface(Injector::class)
                    .build()
            )
            .addSuperinterface(Injector::class)
            .build()
    }

    private fun generateInjectorFactory(scopeName: FqName): FunSpec {
        val componentName = "${scopeName.shortName().asString()}Component"

        return FunSpec.builder("${scopeName.shortName().asString().decapitalize()}InjectorFactory")
            .addModifiers(KModifier.INLINE)
            .addTypeVariable(TypeVariableName("T", Injector::class).copy(reified = true))
            .receiver(ClassName("com.dropbox.kaiken.scoping", "DependencyProviderResolver"))
            .addStatement(
                "return %M { (resolveDependencyProvider() as %L.ParentComponent).create%L() as T }",
                MemberName("com.dropbox.kaiken.runtime", "InjectorFactory"),
                componentName,
                componentName
            )
            .build()
    }

    override fun isApplicable(context: AnvilContext): Boolean = !context.disableComponentMerging

    companion object {
        private const val CUSTOM_SCOPE_FULLY_QUALIFIED_PATH = "com.dropbox.kaiken.annotation.CustomScope"
    }
}
