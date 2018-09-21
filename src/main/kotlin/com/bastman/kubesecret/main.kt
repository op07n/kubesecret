package com.bastman.kubesecret

import com.bastman.kubesecret.command.DecodeSecret
import com.bastman.kubesecret.command.EncodeSecret
import com.bastman.kubesecret.command.GetSecret
import com.bastman.kubesecret.command.ListSecrets
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands

class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>): Unit = Cli()
                .subcommands(ListSecrets(), GetSecret(), DecodeSecret(), EncodeSecret())
                .main(args)

    }
}

class Cli : NoRunCliktCommand(
        help = """|
            |kubesecret
            |
            |a cli tool to read/encode/decode k8s secrets
            |
        """.trimMargin(),
        epilog = """|
            |Build with: Kotlin, GraalVM (native-image)
            |
        """.trimMargin()
)

