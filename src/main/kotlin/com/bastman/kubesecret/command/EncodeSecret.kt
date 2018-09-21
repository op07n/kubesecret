package com.bastman.kubesecret.command

import com.bastman.kubesecret.common.k8s.secret.K8sSecretYml
import com.bastman.kubesecret.common.k8s.secret.K8sSecretYmlPipeline
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional

class EncodeSecret() : CliktCommand(
        help = "base64 encode secret (source e.g.: plain-text.yaml -> sink e.g.: base64-encoded.yaml)",
        name = "base64-encode"
) {
    private val input: String? by argument().optional()

    override fun run(): Unit =
            try {
                pipeline()
            } catch (all: Exception) {
                System.err.println("Command failed! reason: ${all.message} !")
                System.exit(1)
            }

    private fun pipeline(): Unit = input
            .asSourceOrReadStdIn()
            .requireInputIsNotBlankOrExit()
            .encodeYml()
            .let(::println)

    companion object : K8sSecretYml, K8sSecretYmlPipeline
}







