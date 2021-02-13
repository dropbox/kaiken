package com.dropbox.kaiken.processor

import com.dropbox.kaiken.annotation.DaggerInjectable
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.processor.internal.error
import com.dropbox.kaiken.processor.internal.isAndroidActivity
import com.dropbox.kaiken.processor.internal.isAndroidFragment
import com.dropbox.kaiken.processor.internal.validateIsClass
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class InjectableProcessor : AbstractProcessor() {
    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private lateinit var elements: Elements
    private lateinit var types: Types
    private lateinit var annotatedActivityValidator: InjectableActivityValidator
    private lateinit var annotatedActivityWriter: InjectableActivityWriter
    private lateinit var annotatedFragmentValidator: InjectableFragmentValidator
    private lateinit var annotatedFragmentWriter: InjectableFragmentWriter

    override fun init(processingEnvironment: ProcessingEnvironment) {
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
        types = processingEnvironment.typeUtils
        annotatedActivityValidator = InjectableActivityValidator(messager)
        annotatedFragmentValidator = InjectableFragmentValidator(messager)

        initWriter()
    }

    private fun initWriter() {
        annotatedActivityWriter = InjectableActivityWriter(filer, elements)
        annotatedFragmentWriter = InjectableFragmentWriter(filer, elements)
    }

    override fun process(
        set: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        return try {
            processInternal(roundEnvironment)
        } catch (t: Throwable) {
            messager.error(t.message ?: "error while processing")
            true
        }
    }

    private fun processInternal(
        roundEnvironment: RoundEnvironment
    ): Boolean {
        roundEnvironment.getElementsAnnotatedWith(Injectable::class.java).forEach { element ->

            val isClass = element.validateIsClass(messager) {
                "Only classes can be annotated with ${Injectable::class.java.simpleName}"
            }

            if (!isClass) {
                return true
            }

            // We validated it is a class
            val typeElement = element as TypeElement

            safeProcessInjectableElement(typeElement, elements)
        }

        roundEnvironment.getElementsAnnotatedWith(DaggerInjectable::class.java).forEach { element ->

            // val isClass = element.validateIsClass(messager) {
            //     "Only classes can be annotated with ${DaggerInjectable::class.java.simpleName}"
            // }
            //
            // if (!isClass) {
            //     return true
            // }
            //
            // // We validated it is a class
            val typeElement = element as TypeElement

            safeProcessInjectableElement(typeElement, elements)
        }


        return true
    }

    private fun safeProcessInjectableElement(
        typeElement: TypeElement,
        elements: Elements
    ) {
        try {
            when {
                typeElement.isAndroidActivity() -> {
                    processInjectableActivity(typeElement,elements)
                }
                // typeElement.isAndroidFragment() -> {
                //     processInjectableFragment(typeElement)
                // }
                else -> {
                    messager.error(
                        typeElement,
                        "Only Android Activities or Fragments can be annotated with" +
                            " ${Injectable::class.java.simpleName}"
                    )
                }
            }
        } catch (e: IllegalAccessException) {
            messager.error(typeElement, e.message ?: "error")
        }
    }

    private fun processInjectableActivity(
        typeElement: TypeElement,
        elements: Elements) {
        val annotatedActivity = InjectableAnnotatedActivity(typeElement)

        // if (!annotatedActivityValidator.isValid(annotatedActivity)) {
        //     return
        // }

        annotatedActivityWriter.write(annotatedActivity, elements)
    }

    private fun processInjectableFragment(typeElement: TypeElement) {
        val annotatedFragment = InjectableAnnotatedFragment(typeElement)

        if (!annotatedFragmentValidator.isValid(annotatedFragment)) {
            return
        }

        annotatedFragmentWriter.write(annotatedFragment)
    }

    override fun getSupportedAnnotationTypes() = setOf(DaggerInjectable::class.java.canonicalName)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
}
