package io.sn.etoile.impl

import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.createJvmEvaluationConfigurationFromTemplate

// @KotlinScript annotation marks a script definition class
@KotlinScript(
    fileExtension = "etoile.kts",
)
abstract class EtoileScript(
    val args: Array<String>,
) {
    abstract fun execute(): Any?
}

object EtoileScriptHost {
    fun evalFile(scriptFile: File, scriptArgs: Array<String> = emptyArray()): Result<ResultValue> {
        val res = BasicJvmScriptingHost().eval(
            scriptFile.toScriptSource(),
            createJvmCompilationConfigurationFromTemplate<EtoileScript> {
                jvm {
                    dependenciesFromCurrentContext(wholeClasspath = true)
                }
            },
            createJvmEvaluationConfigurationFromTemplate<EtoileScript> {
                constructorArgs(
                    scriptArgs,
                )
            })

        return when (res) {
            is ResultWithDiagnostics.Success -> Result.success(res.value.returnValue)
            is ResultWithDiagnostics.Failure -> {
                res.reports.forEach { println("Error: ${it.message} ${it.location}") }
                Result.failure(Exception("Failed to execute the script"))
            }
        }
    }
}