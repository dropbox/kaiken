package com.dropbox.kaiken.processor

import com.dropbox.kaiken.processor.internal.error
import com.dropbox.kaiken.processor.internal.isAbstract
import com.dropbox.kaiken.processor.internal.isAndroidFragment
import com.dropbox.kaiken.processor.internal.isPublic
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

internal class InjectableFragmentValidator(
    private val messager: Messager
) {
    fun isValid(annotatedFragment: InjectableAnnotatedFragment): Boolean {

        val typeElement = annotatedFragment.annotatedFragmentElement

        return when {
            !typeElement.isPublic() -> {
                messager.error(typeElement, "The class ${typeElement.qualifiedName} is not public")
                false
            }

            typeElement.isAbstract() -> {
                messager.error(typeElement, "The class ${typeElement.qualifiedName} is abstract")
                false
            }

            !typeElement.isAndroidFragment() -> {
                messager.error(
                    typeElement,
                    "The class ${typeElement.qualifiedName} is not an Android fragment"
                )
                messager.printMessage(Diagnostic.Kind.NOTE, typeElement.superclass.kind.name)
                false
            }

            else -> {
                true
            }
        }
    }
}
