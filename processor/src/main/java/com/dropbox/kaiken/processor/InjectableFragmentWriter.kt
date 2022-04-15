package com.dropbox.kaiken.processor

import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.processor.internal.GENERATED_BY_TOP_COMMENT
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf

/**
 * Generates a `Injector` interface definition and an `inject` function for the given annotated
 * fragment.
 *
 * Example:
 *
 * ```
 * @Injectable
 * public class MyFragment : Fragment {
 *     ...
 * }
 * ```
 *
 * will generate:
 *
 * ```
 * interface MyFragmentInjector {
 *    void inject(fragment: MyFragment)
 * }
 *
 * fun MyFragment.inject() {
 *     val injector: MyFragmentInjector = findInjector()
 *     injector.inject(this)
 * }
 *
 * @ContributesTo(Scope::class)
 * interface MainFragmentInjectorScopeContributor: MainFragmentInjector
 * ```
 */
private fun generateInjectExtensionFunction(
    interfaceName: String,
    fragmentType: TypeName,
): FunSpec {
    return FunSpec.builder("inject")
        .receiver(fragmentType)
        .addStatement("val injector: $interfaceName = findInjector()")
        .addStatement("injector.inject(this)")
        .build()
}

@ExperimentalStdlibApi
private fun generateInjectInterfaceSpec(
    interfaceName: String,
    fragmentType: TypeName,
): TypeSpec {
    val interfaceBuilder = TypeSpec.interfaceBuilder(interfaceName)
    return interfaceBuilder
        .addSuperinterface(typeNameOf<Injector>())
        .addModifiers(KModifier.PUBLIC)
        .addFunction(
            FunSpec.builder("inject")
                .addModifiers(KModifier.ABSTRACT)
                .addModifiers(
                    KModifier.PUBLIC
                )
                .addParameter(
                    com.squareup.kotlinpoet.ParameterSpec(
                        "fragment",
                        fragmentType
                    )
                )
                .build()
        ).build()
}

@ExperimentalStdlibApi
internal fun generateFragmentFileSpec(
    pack: String,
    interfaceName: String,
    fragmentType: TypeName,
    shouldGenerateAuthAww: Boolean,
): FileSpec {
    val extensionFunctionSpec = generateInjectExtensionFunction(interfaceName, fragmentType)
    val interfaceSpec = generateInjectInterfaceSpec(interfaceName, fragmentType)

    val fileBuilder = FileSpec.builder(pack, interfaceName)

    return fileBuilder.addComment(GENERATED_BY_TOP_COMMENT)
        .addImport("com.dropbox.kaiken.runtime", "findInjector")
        .addFunction(extensionFunctionSpec)
        .addType(interfaceSpec)
        .build()
}
