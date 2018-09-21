package com.bastman.kubesecret.command

import com.bastman.kubesecret.common.k8s.secret.K8sSecretYml
import com.bastman.kubesecret.util.bufferedInputStreamReader
import com.bastman.kubesecret.util.processBuilder
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class GetSecret : CliktCommand(
        help = "get secret <SECRET_NAME> --base64-decode (requires kubectl)",
        name = "get"
) {
    private val ns: String? by option("--namespace", help = "the k8s namespace")
    private val name: String by argument("--name", help = "secret name")
    private val base64Decode: Boolean by option("--base64-decode", help = "base64 decode .data").flag()

    override fun run() {
        var cmdWithArgs: List<String> = listOf("kubectl get secret $name -o yaml")
        if (ns != null) {
            cmdWithArgs += "--namespace $ns"
        }
        val cmd: String = cmdWithArgs.joinToString(separator = " ")

        try {
            exec(cmd = cmd)
        } catch (all: Exception) {
            System.err.println("Command failed! reason: ${all.message} !")
            System.exit(1)
        }
    }

    private fun exec(cmd: String) = when (base64Decode) {
        false -> execRaw(cmd = cmd)
        true -> execAndBase64Decode(cmd = cmd)
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

    private fun execAndBase64Decode(cmd: String) {
        val builder: ProcessBuilder = processBuilder(cmd = cmd)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
        val process: Process = builder.start()
        val exitCode: Int = process.waitFor()
        if (exitCode != 0) {
            System.exit(exitCode)
        }
        process
                .bufferedInputStreamReader()
                .readText()
                .decodeYml()
                .let(::println)
    }

    companion object : K8sSecretYml
}
