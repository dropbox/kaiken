package com.dropbox.kaiken.processor

import com.dropbox.kaiken.annotation.AutoInjectable
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.processor.internal.error
import com.dropbox.kaiken.processor.internal.implementsInjectionHolder
import com.dropbox.kaiken.processor.internal.isAbstract
import com.dropbox.kaiken.processor.internal.isAndroidActivity
import com.dropbox.kaiken.processor.internal.isPublic
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

internal class InjectableActivityValidator(
    private val messager: Messager
) {
    fun isValid(annotatedActivity: InjectableAnnotatedActivity): Boolean {

        val typeElement = annotatedActivity.annotatedActivityElement

        if (componentAnnotation(typeElement) != null &&
            daggerInjectableAnnotation(typeElement) != null){
            messager.error(
                    typeElement,
                    "DaggerInjectable generates its own component, please remove @Injectable annotation"
            )
            return false
        }

        return when {
            !typeElement.isPublic() -> {
                messager.error(
                    typeElement,
                    "The class ${typeElement.qualifiedName} is not public"
                )
                false
            }

            typeElement.isAbstract() -> {
                messager.error(
                    typeElement,
                    "The class ${typeElement.qualifiedName} is abstract"
                )
                false
            }

            !typeElement.isAndroidActivity() -> {
                messager.error(
                    typeElement,
                    "The class ${typeElement.qualifiedName} is not an Android activity"
                )
                messager.printMessage(Diagnostic.Kind.NOTE, typeElement.superclass.kind.name)
                false
            }

            !typeElement.implementsInjectionHolder() -> {
                messager.error(
                    typeElement,
                    "The class ${typeElement.qualifiedName} does not implement" +
                        " DependencyProviderResolver"
                )
                typeElement.interfaces.forEach {
                    messager.printMessage(Diagnostic.Kind.NOTE, it.kind.name)
                }
                false
            }

            else -> {
                true
            }
        }
    }

    private fun daggerInjectableAnnotation(typeElement: TypeElement): ClassName? {
        if (typeElement.getAnnotation(AutoInjectable::class.java) == null) return null
        return typeElement
            .getAnnotationClassValue<AutoInjectable> { dependency }
            .asTypeName() as ClassName?
    }

    private fun componentAnnotation(typeElement: TypeElement): ClassName? {
        if (typeElement.getAnnotation(Injectable::class.java) == null) return null
        return typeElement
                .getAnnotationClassValue<Injectable> { COMPONENT }
                .asTypeName() as ClassName?
    }
}
