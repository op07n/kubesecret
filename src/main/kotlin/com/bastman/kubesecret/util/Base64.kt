package com.bastman.kubesecret.util

import java.util.*

object Base64Codec {
    private val encoder: Base64.Encoder = Base64.getEncoder()
    private val decoder: Base64.Decoder = Base64.getDecoder()
    fun encode(source: String): String = encoder.encodeToString(source.toByteArray())
    fun decode(source: String): String = decoder.decode(source).toString(Charsets.UTF_8)
}
