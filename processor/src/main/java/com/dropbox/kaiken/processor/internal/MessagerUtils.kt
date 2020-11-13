package com.dropbox.kaiken.processor.internal

import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.tools.Diagnostic

internal fun Messager.error(message: String) {
    this.printMessage(Diagnostic.Kind.ERROR, message)
}

internal fun Messager.error(element: Element, message: String) {
    this.printMessage(Diagnostic.Kind.ERROR, message, element)
}
