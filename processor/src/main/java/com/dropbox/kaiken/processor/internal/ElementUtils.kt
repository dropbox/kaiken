package com.dropbox.kaiken.processor.internal

import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.SimpleTypeVisitor6
import javax.tools.Diagnostic

internal fun TypeElement.isPublic() = modifiers.contains(Modifier.PUBLIC)

internal fun TypeElement.isAbstract() = modifiers.contains(Modifier.ABSTRACT)

internal fun Element.isClass() = kind.isClass

/**
 * Validated whether or not the element is of kind class. Returns true if it is, if it isn't
 * it writes an error message to the given `messager`.
 */
internal fun Element.validateIsClass(messager: Messager, errorMessage: () -> String): Boolean {
    messager.printMessage(Diagnostic.Kind.NOTE, toString())

    if (!isClass()) {
        messager.error(this, errorMessage())
        return false
    }

    return true
}

internal fun TypeElement.isAndroidActivity(): Boolean {
    return isChildOf("androidx.activity.ComponentActivity")
}

internal fun TypeElement.isAndroidFragment(): Boolean {
    return isChildOf("androidx.fragment.app.Fragment")
}

internal fun TypeElement.implementsInjectionHolder(): Boolean {
    return implements("com.dropbox.kaiken.runtime.InjectorHolder")
}

/**
 * Whether not this type is a children of the given candidate ancestor.
 */
internal fun TypeElement.isChildOf(ancestorFullyQualifiedName: String): Boolean {
    if (superclass == null) {
        return false
    }

    val typeVisitor = object : SimpleTypeVisitor6<Boolean, Void>() {
        override fun visitDeclared(type: DeclaredType, p1: Void?): Boolean {
            if (type.asElement().isClass()) {
                val typeElement = (type.asElement() as TypeElement)
                return if (typeElement.qualifiedName.contentEquals(ancestorFullyQualifiedName)) {
                    true
                } else {
                    typeElement.superclass?.accept(this, null) ?: false
                }
            }
            return false
        }
    }

    return superclass.accept(typeVisitor, null)
}

/**
 * Whether not this type implements the given interface (either directly, via its supers, or via
 * other interfaces).
 *
 * @param interfaceName fully qualified name of the candidate interface
 */
internal fun TypeElement.implements(interfaceName: String): Boolean {
    if (this.interfaces.any { it.interfaceIsOrExtendsFrom(interfaceName) }) {
        return true
    }

    if (superclass == null) {
        return false
    }

    val typeVisitor = object : SimpleTypeVisitor6<Boolean, Void>() {
        override fun visitDeclared(type: DeclaredType, p1: Void?): Boolean {
            check(type.asElement().kind.isClass)

            val typeElement = (type.asElement() as TypeElement)
            return typeElement.implements(interfaceName)
        }
    }

    return superclass.accept(typeVisitor, null)
}

/**
 * Whether this interface type element is or extends the given interface
 *
 * @param interfaceName fully qualified name of the candidate interface
 */
private fun TypeMirror.interfaceIsOrExtendsFrom(interfaceName: String): Boolean {
    val typeVisitor = object : SimpleTypeVisitor6<Boolean, Void>() {
        override fun visitDeclared(type: DeclaredType, p1: Void?): Boolean {
            check(type.asElement().kind.isInterface)

            val typeElement = (type.asElement() as TypeElement)
            return if (typeElement.qualifiedName.contentEquals(interfaceName)) {
                true
            } else {
                typeElement.interfaces.any { it.accept(this, null) }
            }
        }
    }

    return this.accept(typeVisitor, null)
}
