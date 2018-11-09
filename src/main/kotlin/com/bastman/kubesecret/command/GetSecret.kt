package com.bastman.kubesecret.command

import com.bastman.kubesecret.common.k8s.secret.K8sSecretYml
import com.bastman.kubesecret.util.bufferedInputStreamReader
import com.bastman.kubesecret.util.processBuilder
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import java.time.Instant

enum class Transform(val cliValue: String) {
    NONE(cliValue = "none"),
    BASE64_DECODE(cliValue = "base64-decode"),
    BASE64_ENCODE(cliValue = "base64-encode")
    ;

    companion object {
        fun allCliValues(): List<String> = values().toList().map { it.cliValue }
        fun default(): Transform = NONE
        fun ofCliValue(cliValue: String): Transform = values().first { it.cliValue == cliValue }
    }
}

enum class OutputFormat(val cliValue: String) {
    YML(cliValue = "yml"),
    BASH(cliValue = "bash")
    ;

    companion object {
        fun allCliValues(): List<String> = values().toList().map { it.cliValue }
        fun default(): OutputFormat = OutputFormat.YML
        fun ofCliValue(cliValue: String): OutputFormat = values().first { it.cliValue == cliValue }
    }
}

class GetSecret : CliktCommand(
        help = "get secret <SECRET_NAME> (requires kubectl) --transform=base64-decode --output-format=yml",
        name = "get"
) {
    private val ns: String? by option("--namespace", help = "the k8s namespace")
    private val name: String by argument("--name", help = "secret name")

    private val format: String? by option(
            "--output-format",
            help = "one of ${OutputFormat.allCliValues()} - default: ${OutputFormat.default().cliValue}"
    ).default(OutputFormat.default().cliValue)

    private val transform: String? by option(
            "--transform",
            help = "one of ${Transform.allCliValues()} - default: ${Transform.default().cliValue}"
    ).default(Transform.default().cliValue)


    override fun run() {
        var cmdWithArgs: List<String> = listOf("kubectl get secret $name -o yaml")
        if (ns != null) {
            cmdWithArgs += "--namespace $ns"
        }
        val cmd: String = cmdWithArgs.joinToString(separator = " ")

        try {
            val transfomer: Transform = (transform ?: Transform.default().cliValue)
                    .let {
                        try {
                            Transform.ofCliValue(it)
                        } catch (all: Exception) {
                            error(
                                    "Invalid value for option --transform !" +
                                            " must be one of: ${Transform.allCliValues()}"
                            )
                        }
                    }
            val outputFormat: OutputFormat = (format ?: OutputFormat.default().cliValue)
                    .let {
                        try {
                            OutputFormat.ofCliValue(it)
                        } catch (all: Exception) {
                            error(
                                    "Invalid value for option --output-format !" +
                                            " must be one of: ${OutputFormat.allCliValues()}"
                            )
                        }
                    }

            exec(cmd = cmd, outputFormat = outputFormat, transform = transfomer)
        } catch (all: Exception) {
            cliExit("Command failed! reason: ${all.message} !")
        }
    }

    private fun exec(cmd: String, outputFormat: OutputFormat, transform: Transform) {
        val raw: Boolean = (transform == Transform.NONE) && (outputFormat == OutputFormat.YML)
        when (raw) {
            true -> execRaw(cmd = cmd)
            false -> execAndTransform(cmd = cmd, transform = transform, outputFormat = outputFormat)
        }
    }

    private fun execRaw(cmd: String) {
        val builder: ProcessBuilder = processBuilder(cmd = cmd)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        val process: Process = builder.start()
        val exitCode: Int = process.waitFor()
        if (exitCode != 0) {
            System.exit(exitCode)
        }
    }

    private fun execAndTransform(cmd: String, transform: Transform, outputFormat: OutputFormat) {
        val builder: ProcessBuilder = processBuilder(cmd = cmd)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
        val process: Process = builder.start()
        val exitCode: Int = process.waitFor()
        if (exitCode != 0) {
            System.exit(exitCode)
        }

        process.bufferedInputStreamReader()
                .readText()
                .parseYml()
                .let { data ->
                    when (transform) {
                        Transform.NONE -> data
                        Transform.BASE64_DECODE -> data.transformYml { it.base64Decode() }
                        Transform.BASE64_ENCODE -> data.transformYml { it.base64Encode() }
                    }
                }.let {
                    when (outputFormat) {
                        OutputFormat.YML -> it.toYml()
                        OutputFormat.BASH -> it.toBashProfile(prolog = bashProfileProlog())
                    }
                }.let(::println)
    }

    private fun bashProfileProlog():String {
        val namespacedSecretName:String = listOf(ns, name).filterNotNull().joinToString(separator = "/")
        return "# kubesecret: $namespacedSecretName (${Instant.now()})\n"
    }

    companion object : K8sSecretYml
}


private fun cliExit(msg: String) {
    System.err.println(msg)
    System.exit(1)
}