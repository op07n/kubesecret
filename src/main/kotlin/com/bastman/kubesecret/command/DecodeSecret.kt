package com.bastman.kubesecret.command

import com.bastman.kubesecret.common.k8sDecode.base64DecodeK8sSecretYml
import com.bastman.kubesecret.util.Base64Codec
import com.bastman.kubesecret.util.readStdIn
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional

private typealias HashMap = Map<String, Any?>
private typealias MutableHashMap = MutableMap<String, Any?>

class DecodeSecret() : CliktCommand(
        help = "base64 decode secret",
        name = "base64-decode"
) {
    private val input: String? by argument().optional()

    override fun run() {
        val sourceText: String = when (input) {
            null -> readStdIn()
            else -> input ?: ""
        }
        if (sourceText.isBlank()) {
            System.err.println("INPUT must not be empty!")
            System.exit(1)
        }
        try {
            exec(sourceText = sourceText)
        } catch (all: Exception) {
            System.err.println("Command failed! reason: ${all.message} !")
            System.exit(1)
        }
    }

    private fun exec(sourceText: String) {
        val sinkYmlText: String = base64DecodeK8sSecretYml(sourceText)
        println(sinkYmlText)
    }
}

private fun String.base64Decode(): String = Base64Codec.decode(this)
private fun HashMap.withKey(key: String, value: Any?): HashMap {
    val hashMap: MutableHashMap = this.toMutableMap()
    hashMap[key] = value
    return hashMap
}


