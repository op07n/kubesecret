package com.example.demo


import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

fun readStdIn(): String {
    val buffReader = BufferedReader(InputStreamReader(System.`in`))
    return buffReader.lineSequence().joinToString(separator = System.lineSeparator())
}

object Base64Codec {
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()
    fun encode(source:String):String = encoder.encodeToString(source.toByteArray())
    fun decode(source:String):String = decoder.decode(source).toString(Charsets.UTF_8)
}
