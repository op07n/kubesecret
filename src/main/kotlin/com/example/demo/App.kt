package com.example.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import java.util.*

val YAML = ObjectMapper(YAMLFactory()).findAndRegisterModules()
val JSON = jacksonObjectMapper().findAndRegisterModules()
val B64 = Base64Codec

private typealias HashMap = Map<String, Any?>

class App {

    fun exec() = yml1()

    fun json1() {
        val s = """
                {
                    "bar":"XXXXXX",
                    "data": {
                        "x1":"X1",
                        "x2":"X2"
                    }
                }
            """.trimIndent()
        println(s)

        val foo = Foo(
                bar = "123"
        )
        println(foo)

        val asJson: String = JSON.writeValueAsString(foo)

        println("as json: $asJson")

        println("===== deserialize ....")
        val f: HashMap = JSON.readValue(s, jacksonTypeRef<HashMap>())
        println(f)
    }

    fun yml1() {
        val sourceText = readStdIn()
        println(sourceText)
        println("===== deserialize ....")
        val source: HashMap = YAML.readValue(sourceText, jacksonTypeRef<HashMap>())
        println(source)

        println("=== transform .data ===")
       val data:HashMap = source["data"] as HashMap
        println("=> .data: $data")
        println("=> ...... ")
        val sinkData = data.map {
            val key:String = it.key
            val value:String = it.value as String

            //println("data.$key => (plain) $value => decoded ${B64.decode(value)}")
            Pair(it.key,B64.decode(value) )
        }.toMap()

        val sink = source.toMutableMap()
        sink["data"] = sinkData
        println("===== serialize ....")
        val sinkText = YAML.writeValueAsString(sink)
        println(sinkText)
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) = App().exec()


    }
}

data class Foo(val bar: String)

