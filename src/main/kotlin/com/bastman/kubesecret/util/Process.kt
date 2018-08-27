package com.bastman.kubesecret.util


import java.io.BufferedReader
import java.io.InputStreamReader

// https://stackoverflow.com/questions/35421699/how-to-invoke-external-command-from-within-kotlin-code
// https://www.baeldung.com/run-shell-command-in-java

fun processBuilder(cmd: String): ProcessBuilder = ProcessBuilder(*cmd.split(" ").toTypedArray())


fun Process.bufferedInputStreamReader(): BufferedReader =
        BufferedReader(InputStreamReader(inputStream))

fun readStdIn(): String {
    val buffReader = BufferedReader(InputStreamReader(System.`in`))
    return buffReader.readText()
    //return buffReader.lineSequence().joinToString(separator = System.lineSeparator())
}
