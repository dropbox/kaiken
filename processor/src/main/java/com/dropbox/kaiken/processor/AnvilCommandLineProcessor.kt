package com.dropbox.kaiken.processor

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * Parses arguments from the Gradle plugin for the compiler plugin.
 */
@AutoService(CommandLineProcessor::class)
class AnvilCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "com.dropbox.kaiken.processor"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
            CliOption(
                    optionName = srcGenDirName,
                    valueDescription = "<file-path>",
                    description = "Path to directory in which Anvil specific code should be generated",
                    required = true,
                    allowMultipleOccurrences = false
            ),
            CliOption(
                    optionName = generateDaggerFactoriesName,
                    valueDescription = "<true|false>",
                    description = "Whether Anvil should generate Factory classes that the Dagger " +
                            "annotation processor would generate for @Provides methods and @Inject " +
                            "constructors.",
                    required = false,
                    allowMultipleOccurrences = false
            )
    )

    override fun processOption(
            option: AbstractCliOption,
            value: String,
            configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            srcGenDirName -> configuration.put(srcGenDirKey, value)
            generateDaggerFactoriesName ->
                configuration.put(generateDaggerFactoriesKey, value.toBoolean())
        }
    }
}