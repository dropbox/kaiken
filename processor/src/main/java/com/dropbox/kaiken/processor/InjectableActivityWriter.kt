package com.dropbox.kaiken.processor

import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.processor.internal.GENERATED_BY_TOP_COMMENT
import com.dropbox.kaiken.processor.internal.generateContributesInjector
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf
import kotlin.reflect.KClass

/**
 * Generates a `Injector` interface definition and an `inject` function for the given annotated
 * activity.
 *
 * Example:
 *
 * ```
 * @Injectable
 * public class MyActivity : AppCompatActivity() {
 *     ...
 * }
 * ```
 *
 * will generate:
 *
 * ```
 * interface MyActivityInjector {
 *    void inject(activity: MyActivity)
 * }
 *
 * fun MyActivity.inject() {
 *     val injector: MyActivityInjector = this.locateInjector()
 *     injector.inject(this)
 * }
 *
 * @ContributesTo(Scope::class)
 * interface MainActivityInjectorScopeContributor: MainActivityInjector
 * ```
 */
private fun generateInjectExtensionFunctionForActivity(
    interfaceName: String,
    typeName: com.squareup.kotlinpoet.TypeName
): FunSpec {
    return FunSpec.builder("inject")
        .receiver(typeName)
        .addStatement("val activityInjector: $interfaceName = this.locateInjector()")
        .addStatement("activityInjector.inject(this)")
        .build()
}

@ExperimentalStdlibApi
private fun generateInjectInterfaceForActivity(
    interfaceName: String,
    typeName: com.squareup.kotlinpoet.TypeName
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
                        "activity",
                        typeName
                    )
                )
                .build()
        ).build()
}

@ExperimentalStdlibApi
internal fun generateActivityFileSpec(
    pack: String,
    interfaceName: String,
    typeName: TypeName,
    authAwarenessScope: KClass<out Any>?
): FileSpec {
    val extensionFunctionSpec = generateInjectExtensionFunctionForActivity(
        interfaceName, typeName
    )
    val interfaceTypeSpec = generateInjectInterfaceForActivity(
        interfaceName, typeName
    )
    val contributesInjectorTypeSpec = generateContributesInjector(
        pack, interfaceName, authAwarenessScope
    )
    val fileBuilder = FileSpec.builder(pack, interfaceName)

    return fileBuilder.addComment(GENERATED_BY_TOP_COMMENT)
        .addFunction(extensionFunctionSpec)
        .addType(interfaceTypeSpec)
        .apply {
            if (contributesInjectorTypeSpec != null) {
                addType(contributesInjectorTypeSpec)
            }
        }
        .build()
}
