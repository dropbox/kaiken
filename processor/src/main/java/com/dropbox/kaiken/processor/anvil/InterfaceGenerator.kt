package com.dropbox.kaiken.processor.anvil

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

internal class InterfaceGenerator : CodeGenerator {
    override fun generateCode(
            codeGenDir: File,
            module: ModuleDescriptor,
            projectFiles: Collection<KtFile>
    ): Collection<CodeGenerator.GeneratedFile> {
        return projectFiles.asSequence()
                .flatMap { it.classesAndInnerClasses() }
                .filter { it.hasAnnotation(singletonAnnotation) } //using singleton just as trial
                .map { clazz: KtClassOrObject ->
                    val packageName = clazz.containingKtFile.packageFqName.asString()
                    val className = clazz.requireFqName()
                            .asString()
                    //Getting annotation
                    //val klass: KtClassOrObject = ...
                    //val descriptor = module.resolveClassByFqName(klass.getFqNameSomehow())!!
                    //descriptor.getAllSuperClassifiers().any { it.fqNameSafe == FqName("android.app.Activity") }
                    val simpleName = clazz.name!!
                    val directory = File(codeGenDir, packageName.replace('.', File.separatorChar))
                    val file = File(directory, "Real"+simpleName+"Injector.kt")
                    check(file.parentFile.exists() || file.parentFile.mkdirs()) {
                        "Could not generate package directory: ${file.parentFile}"
                    }

//                    val scope = clazz.scope(contributesToFqName, module)

                    val content =  ""

                    file.writeText(content)

                    CodeGenerator.GeneratedFile(file, content)
                }
                .toList()
    }
}
