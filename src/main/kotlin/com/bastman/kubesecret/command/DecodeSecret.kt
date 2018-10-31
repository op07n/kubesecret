package com.bastman.kubesecret.command

import com.bastman.kubesecret.common.k8s.secret.K8sSecretYml
import com.bastman.kubesecret.common.k8s.secret.K8sSecretYmlPipeline
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional

class DecodeSecret() : CliktCommand(
        help = "base64 decode secret (source e.g.: base64-encoded.yaml -> sink e.g.: plain-text.yaml)",
        name = "base64-decode"
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
            .decodeYml()
            .toYml()
            .let(::println)

    companion object : K8sSecretYml, K8sSecretYmlPipeline
}



