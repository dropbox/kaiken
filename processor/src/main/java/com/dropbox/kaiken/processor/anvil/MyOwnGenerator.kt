package com.dropbox.kaiken.processor.anvil

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import java.io.File

/**
 * Generates a hint for each contributed class in the `hint.anvil` packages. This allows the
 * compiler plugin to find all contributed classes a lot faster when merging modules and component
 * interfaces.
 */
internal class MyOwnGenerator : CodeGenerator {
    override fun generateCode(
            codeGenDir: File,
            module: ModuleDescriptor,
            projectFiles: Collection<KtFile>
    ): Collection<CodeGenerator.GeneratedFile> {

        return projectFiles.asSequence()
                .flatMap { it.classesAndInnerClasses() }
                .filter { it.hasAnnotation(autoInjectableFqname) }
                .map { clazz ->
                    val packageName = clazz.containingKtFile.packageFqName.asString()
                    val className = clazz.requireFqName()
                            .asString()

                    val directory = File(codeGenDir, packageName.replace('.', File.separatorChar))
                    val file = File(directory, "${className.replace('.', '_')}.kt")
                    check(file.parentFile.exists() || file.parentFile.mkdirs()) {
                        "Could not generate package directory: ${file.parentFile}"
                    }

                    val scope = clazz.scope(contributesToFqName, module)

                    val content = """
              haha this is Mike
          """.trimIndent()
                    file.writeText(content)

                    CodeGenerator.GeneratedFile(file, content)
                }
                .toList()
    }
}

