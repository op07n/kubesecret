package com.bastman.kubesecret.command


import com.bastman.kubesecret.util.processBuilder
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option


class ListSecrets() : CliktCommand(
        help = "list secrets (requires kubectl)",
        name = "list"
) {
    private val ns: String? by option("--namespace", help = "the k8s namespace")
    override fun run() {
        var cmdWithArgs: List<String> = listOf("kubectl get secrets")
        if (ns != null) {
            cmdWithArgs += "--namespace $ns"
        }

        val builder: ProcessBuilder = processBuilder(cmd = cmdWithArgs.joinToString(separator = " "))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
        val process: Process = builder.start()
        val exitCode: Int = process.waitFor()
        System.exit(exitCode)
    }
}

