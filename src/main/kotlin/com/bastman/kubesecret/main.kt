package com.bastman.kubesecret

import com.bastman.kubesecret.command.DecodeSecret
import com.bastman.kubesecret.command.GetSecret
import com.bastman.kubesecret.command.ListSecrets
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands

class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val cli = Cli()
            cli.subcommands(ListSecrets(), GetSecret(), DecodeSecret())
            cli.main(args)
        }
    }
}

class Cli : NoRunCliktCommand(
        help = """|
            |kubesecret
            |
            |a cli tool read/decode k8s secrets
            |
        """.trimMargin(),
        epilog = """|
            |Build with: Kotlin, GraalVM (native-image)
            |
        """.trimMargin()
)

