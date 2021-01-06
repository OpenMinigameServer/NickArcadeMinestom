package io.github.nickacpt.nickarcade.utils.commands

import net.minestom.server.command.CommandSender

interface CommandCompletableWithSender {

    fun performCompletion(sender: CommandSender, text: String): Array<String>

}