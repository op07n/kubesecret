package com.bastman.kubesecret.common.k8sDecode


import com.bastman.kubesecret.util.Base64Codec
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef

private val YAML = ObjectMapper(YAMLFactory()).findAndRegisterModules()
fun base64DecodeK8sSecretYml(sourceText: String): String {
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
    val sinkData = sourceData
            .map { Pair(it.key, (it.value as String).base64Decode()) }
            .toMap()
    val sink: HashMap = source.withKey(key = "data", value = sinkData)
    val sinkText: String = YAML.writeValueAsString(sink)
    return sinkText
}
private typealias HashMap = Map<String, Any?>
private typealias MutableHashMap = MutableMap<String, Any?>

private fun String.base64Decode(): String = Base64Codec.decode(this)
private fun HashMap.withKey(key: String, value: Any?): HashMap {
    val hashMap: MutableHashMap = this.toMutableMap()
    hashMap[key] = value
    return hashMap
}
