package com.bastman.kubesecret.common.k8s.secret

import com.bastman.kubesecret.util.Base64Codec
import com.bastman.kubesecret.util.readStdIn
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef

interface K8sSecretYml {
    fun String.encodeYml(): HashMap = transformYml(sourceText = this, mapValue = {it.base64Encode()})
    fun String.decodeYml(): HashMap = transformYml(sourceText = this, mapValue = {it.base64Decode()})
    fun String.parseYml(): HashMap = transformYml(sourceText = this, mapValue = {it.doNothing()})
    fun HashMap.toYml():String = YAML.writeValueAsString(this)
    fun HashMap.transformYml(mapValue: (String) -> String):HashMap = transformYml(source = this, mapValue=mapValue)
    fun HashMap.toBashProfile(prolog:String =""):String {
        val sourceData = this["data"] as HashMap
        val lines:List<String> = sourceData.map {
            val value: String = it.value as String
            "export ${it.key}=$value"
        }
        return "$prolog${lines.joinToString(separator = "\n")}"
    }

    fun String.doNothing():String = this
    fun String.base64Decode(): String = Base64Codec.decode(this)
    fun String.base64Encode(): String = Base64Codec.encode(this)
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


private fun transformYml(source: HashMap, mapValue: (String) -> String): HashMap {
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
    return sink
}

private fun transformYml(sourceText: String, mapValue: (String) -> String): HashMap {
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
    return sink
}


private typealias HashMap = Map<String, Any?>
private typealias MutableHashMap = MutableMap<String, Any?>


private fun HashMap.withKey(key: String, value: Any?): HashMap {
    val hashMap: MutableHashMap = this.toMutableMap()
    hashMap[key] = value
    return hashMap
}
