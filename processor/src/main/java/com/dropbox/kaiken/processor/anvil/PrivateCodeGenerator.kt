package com.dropbox.kaiken.processor.anvil

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

internal abstract class PrivateCodeGenerator : CodeGenerator {
  final override fun generateCode(
          codeGenDir: File,
          module: ModuleDescriptor,
          projectFiles: Collection<KtFile>
  ): Collection<CodeGenerator.GeneratedFile> {
    generateCodePrivate(codeGenDir, module, projectFiles)
    return emptyList()
  }

  protected abstract fun generateCodePrivate(
          codeGenDir: File,
          module: ModuleDescriptor,
          projectFiles: Collection<KtFile>
  )
}
