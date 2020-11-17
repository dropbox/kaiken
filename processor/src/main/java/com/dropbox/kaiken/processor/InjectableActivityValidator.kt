package com.dropbox.kaiken.processor

import com.dropbox.kaiken.processor.internal.error
import com.dropbox.kaiken.processor.internal.implementsInjectionHolder
import com.dropbox.kaiken.processor.internal.isAbstract
import com.dropbox.kaiken.processor.internal.isAndroidActivity
import com.dropbox.kaiken.processor.internal.isPublic
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

internal class InjectableActivityValidator(
    private val messager: Messager
) {
    fun isValid(annotatedActivity: InjectableAnnotatedActivity): Boolean {

        val typeElement = annotatedActivity.annotatedActivityElement

        return when {
            !typeElement.isPublic() -> {
                messager.error(typeElement, "The class ${typeElement.qualifiedName} is not public")
                false
            }

            typeElement.isAbstract() -> {
                messager.error(typeElement, "The class ${typeElement.qualifiedName} is abstract")
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
}
