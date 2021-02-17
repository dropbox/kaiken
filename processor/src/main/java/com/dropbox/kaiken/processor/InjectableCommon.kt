package com.dropbox.kaiken.processor

import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.annotation.AutoInjectable
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.processor.internal.GENERATED_BY_TOP_COMMENT
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import kotlin.reflect.KClass

internal fun generateInjectorInterfaceFileSpec(
        pack: String,
        interfaceName: String,
        paramName: String,
        targetType: TypeMirror,
        className: com.squareup.kotlinpoet.ClassName?
): FileSpec {
    val interfaceSpec =
            generateInjectorInterface(interfaceName, paramName, targetType, className)

    return FileSpec.builder(pack, "real$interfaceName")
            .addComment(GENERATED_BY_TOP_COMMENT)
            .addType(interfaceSpec)
            .build()
}

private fun generateInjectorInterface(
        interfaceName: String,
        paramName: String,
        targetType: TypeMirror,
        className: com.squareup.kotlinpoet.ClassName?
): com.squareup.kotlinpoet.TypeSpec {
    val interfaceBuilder = com.squareup.kotlinpoet.TypeSpec.interfaceBuilder(interfaceName)

    val injectorInterface = interfaceBuilder
            .addSuperinterface(Injector::class.java)
            .addFunction(
                    FunSpec.builder("inject")
                            .addModifiers(KModifier.ABSTRACT)
                            .addParameter(
                                    com.squareup.kotlinpoet.ParameterSpec.builder(
                                            "activity",
                                            targetType.asTypeName()
                                    )
                                            .build()
                            )
                            .build()
            )

    if (anvilOnPath())
        injectorInterface.addAnnotation(
                com.squareup.kotlinpoet.AnnotationSpec.builder(
                        com.squareup.kotlinpoet.ClassName(
                                "com.squareup.anvil.annotations",
                                "ContributesTo"
                        )
                )
                        .addMember(
                                "scope = %T::class",
                                className!!
                        )
                        .build()
        )
    return injectorInterface.build()
}

internal fun resolveInjectorInterfaceName(
        typeElement: TypeElement
): String = "${typeElement.simpleName}Injector"


fun generateAnvilParts(annotatedActivityTypeElement: TypeElement, elements: Elements, annotatedActivityType: TypeMirror, pack: String, filer: Filer, dependendecies: ClassName?): Pair<com.squareup.kotlinpoet.ClassName?, com.squareup.kotlinpoet.ClassName?> {
    var injectableAnnotationComponent = injectableAnnotation(annotatedActivityTypeElement)
    val scope: TypeMirror?
    var anvilScope: com.squareup.kotlinpoet.ClassName? = null

    //if provided component is an anvil component we should grab the scope
    if (injectableAnnotationComponent != null && anvilOnPath()) {
        scope = componentAnvilScope(elements, injectableAnnotationComponent)
        if (scope != null) anvilScope = scope.asTypeName() as com.squareup.kotlinpoet.ClassName
    }
    //when no component provided or no anvil scope found we will create an anvil scope
    if (anvilOnPath() && anvilScope == null) {
        val activityTypeName = annotatedActivityType.asTypeName() as com.squareup.kotlinpoet.ClassName
        anvilScope = activityTypeName.peerClass("A${activityTypeName.simpleName}Scope")
    }
    //no component provided, we need to make one
    if (dependendecies != null) {
        //make sure to set a component dependency with annotated dependency value
        generateComponent(pack, annotatedActivityType, dependendecies, filer)
        val className = annotatedActivityType.asTypeName() as com.squareup.kotlinpoet.ClassName
        //we made a component and want to pass that to other generators
        injectableAnnotationComponent =
                className.peerClass(className.simpleName + "Component")
    }
    return Pair(injectableAnnotationComponent, anvilScope)
}

private fun createInjectExtension(activityTypeName: ClassName, componentClass: ClassName, fileSpecBuilder: FileSpec.Builder) {
    val injectorClass = activityTypeName.peerClass("${activityTypeName.simpleName}Injector")
    val injectorFactory =
            ClassName("com.dropbox.kaiken.runtime", "InjectorFactory")
                    .parameterizedBy(
                            injectorClass
                    )

    val anonymousClass = TypeSpec
            .anonymousClassBuilder()
            .addSuperinterface(injectorFactory)
            .addFunction(
                    FunSpec.builder("createInjector")
                            .addModifiers(KModifier.OVERRIDE)
                            .addStatement(
                                    "return Dagger${componentClass.simpleName}.factory().create(resolveDependencyProvider()) as %T",
                                    injectorClass
                            )
                            .build()
            )
            .build()
    fileSpecBuilder.addFunction(
            FunSpec.builder("injector")
                    .receiver(Class.forName("com.dropbox.kaiken.scoping.DependencyProviderResolver"))
                    .returns(injectorFactory)
                    .addStatement("return %L", anonymousClass).build()
    )
}

private fun componentAnvilScope(
        elements: Elements,
        injectableAnnotation: com.squareup.kotlinpoet.ClassName
): TypeMirror? {
    return try {
        elements
                .getTypeElement(injectableAnnotation.canonicalName)
                .getAnnotationClassValue<MergeComponent> { scope }
    } catch (e: Exception) {
        null
    }
}


private fun injectableAnnotation(annotatedActivityTypeElement: TypeElement): com.squareup.kotlinpoet.ClassName? {
    return try {
        annotatedActivityTypeElement
                .getAnnotationClassValue<Injectable> { COMPONENT }
                .asTypeName() as com.squareup.kotlinpoet.ClassName?
    } catch (e: Exception) {
        null
    }
}

fun dependenciesValue(annotatedActivityTypeElement: TypeElement): com.squareup.kotlinpoet.ClassName? {
    return try {
        annotatedActivityTypeElement
                .getAnnotationClassValue<AutoInjectable> { dependency }
                .asTypeName() as com.squareup.kotlinpoet.ClassName?
    } catch (e: Exception) {
        null
    }
}

private fun generateComponent(
        pack: String,
        annotatedActivityType: TypeMirror,
        dependencyClassname: com.squareup.kotlinpoet.ClassName, filer: Filer
): String {
    val activityTypeName = annotatedActivityType.asTypeName() as com.squareup.kotlinpoet.ClassName
    val anvilScope = activityTypeName.peerClass("A${activityTypeName.simpleName}Scope")
    val fileBuilder = FileSpec.builder(pack, activityTypeName.simpleName + "GeneratedComponent")
            .addType(daggerScope(activityTypeName))
            .addType(com.squareup.kotlinpoet.TypeSpec.classBuilder(anvilScope).build())
            .addType(featureComponent(activityTypeName, dependencyClassname))
    createInjectExtension(activityTypeName, activityTypeName.peerClass("${activityTypeName.simpleName}Component"), fileBuilder)
    fileBuilder
            .build().writeTo(filer)
    return activityTypeName.canonicalName + "GeneratedComponent"
}


private fun daggerScope(activityTypeName: com.squareup.kotlinpoet.ClassName): com.squareup.kotlinpoet.TypeSpec {
    val daggerScope = activityTypeName.peerClass("${activityTypeName.simpleName}Scope")
    return com.squareup.kotlinpoet.TypeSpec.annotationBuilder(daggerScope)
            .addAnnotation(com.squareup.kotlinpoet.ClassName("javax.inject", "Scope"))
            .build()
}

private fun featureComponent(
        activityTypeName: com.squareup.kotlinpoet.ClassName,
        dependencyClassname: com.squareup.kotlinpoet.ClassName
): com.squareup.kotlinpoet.TypeSpec {
    val daggerComponent: com.squareup.kotlinpoet.ClassName =
            activityTypeName.peerClass("${activityTypeName.simpleName}Component")
    val aAnvilScope = activityTypeName.peerClass("A${activityTypeName.simpleName}Scope")
    val componentType =
            if (anvilOnPath()) com.squareup.kotlinpoet.ClassName("com.squareup.anvil.annotations", "MergeComponent")
            else com.squareup.kotlinpoet.ClassName("dagger", "Component")

    val factory =
            com.squareup.kotlinpoet.TypeSpec.interfaceBuilder(daggerComponent.peerClass("Factory"))
                    .addAnnotation(
                            AnnotationSpec.builder(com.squareup.kotlinpoet.ClassName("dagger", "Component", "Factory")).build()
                    )
                    .addFunction(
                            FunSpec.builder("create")
                                    .addModifiers(KModifier.ABSTRACT)
                                    .addParameter(dependencyClassname.simpleName, dependencyClassname)
                                    .returns(daggerComponent)
                                    .build()
                    ).build()
    val annotationMembers = AnnotationSpec.builder(componentType)
            .addMember("dependencies = [%T::class]", dependencyClassname)
    if (anvilOnPath()) annotationMembers.addMember("scope = %T::class", aAnvilScope)
    return com.squareup.kotlinpoet.TypeSpec.interfaceBuilder(daggerComponent)
            .addType(factory)
            .addAnnotation(activityTypeName.peerClass("${activityTypeName.simpleName}Scope"))
            .addAnnotation(
                    annotationMembers
                            .build()
            )
            .build()
}

fun anvilOnPath(): Boolean {
    try {
        Class.forName("com.squareup.anvil.annotations.ContributesTo")
    } catch (e: ClassNotFoundException) {
        return false
    }
    return false
}

inline fun <reified T : Annotation> Element.getAnnotationClassValue(f: T.() -> KClass<*>) =
        try {
            getAnnotation(T::class.java).f()
            throw Exception("Expected to get a MirroredTypeException")
        } catch (e: MirroredTypeException) {
            e.typeMirror
        }

