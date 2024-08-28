package gg.ninjagaming.advancedlobby.commands

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChatClearCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command is available for players only.")
            return true
        }

        if (!sender.hasPermission("advancedlobby.commands.chatclear")) {
            sender.sendMessage(Locale.NO_PERMISSION.getMessage(sender))
            return true
        }

        for (otherPlayer in Bukkit.getOnlinePlayers()) {
            if (otherPlayer.hasPermission("advancedlobby.chatclear.bypass"))
                otherPlayer.sendMessage(Locale.COMMAND_CHATCLEAR_GLOBAL.getMessage(sender).replace("%player%", AdvancedLobby.getName(sender)))


            for (i in 0..149) {
                otherPlayer.sendMessage("")
            }
        }
        return true
    }
}