package com.bastman.kubesecret.common.k8s.secret

import com.bastman.kubesecret.util.Base64Codec
import com.bastman.kubesecret.util.readStdIn
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef

interface K8sSecretYml {
    fun String.encodeYml(): String = transformYml(sourceText = this, mapValue = String::base64Encode)
    fun String.decodeYml(): String = transformYml(sourceText = this, mapValue = String::base64Decode)
}

interface K8sSecretYmlPipeline {
    fun String?.asSourceOrReadStdIn(): String =
            when (this) {
                null -> readStdIn()
                else -> this
            }

    fun String.requireInputIsNotBlankOrExit(exitCode: Int = 1): String = also {
        if (isBlank()) {
            System.err.println("INPUT must not be empty!")
            System.exit(exitCode)
        }
    }
}

private val YAML: ObjectMapper = ObjectMapper(YAMLFactory()).findAndRegisterModules()

private fun transformYml(sourceText: String, mapValue: (String) -> String): String {
    val source: HashMap = try {
        YAML.readValue(sourceText, jacksonTypeRef<HashMap>())
    } catch (all: Exception) {
        throw RuntimeException("K8s yaml specs: Invalid content.")
    }
    if (!source.containsKey("data")) {
        throw RuntimeException("K8s yaml specs: Invalid .data ! key 'data' does not exist")
    }
    val sourceData: HashMap = try {
        source["data"] as HashMap
    } catch (all: Exception) {
        throw RuntimeException("K8s yaml specs: Invalid .data ! Failed to parse as HashMap.")
    }
    val sinkData: Map<String, String> = sourceData
            .map {
                val value: String = mapValue((it.value as String))
                Pair(it.key, value)
            }
            .toMap()
    val sink: HashMap = source.withKey(key = "data", value = sinkData)
    val sinkText: String = YAML.writeValueAsString(sink)
    return sinkText
}

private typealias HashMap = Map<String, Any?>
private typealias MutableHashMap = MutableMap<String, Any?>

private fun String.base64Decode(): String = Base64Codec.decode(this)
private fun String.base64Encode(): String = Base64Codec.encode(this)
private fun HashMap.withKey(key: String, value: Any?): HashMap {
    val hashMap: MutableHashMap = this.toMutableMap()
    hashMap[key] = value
    return hashMap
}
