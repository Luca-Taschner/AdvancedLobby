package gg.ninjagaming.advancedlobby.commands

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GlobalMuteCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command is available for players only.")
            return true
        }

        if (!sender.hasPermission("advancedlobby.commands.globalmute")) {
            sender.sendMessage(Locale.NO_PERMISSION.getMessage(sender))
            return true
        }

        if (!AdvancedLobby.globalMute) {
            AdvancedLobby.globalMute = true
            for (players in Bukkit.getOnlinePlayers()) {
                players.sendMessage(
                    Locale.COMMAND_GLOBALMUTE_ENABLE.getMessage(sender).replace(
                        "%player%", AdvancedLobby.getName(
                            sender
                        )
                    )
                )
            }
            return true
        }

        AdvancedLobby.globalMute = false
        for (players in Bukkit.getOnlinePlayers()) {
            players.sendMessage(
                Locale.COMMAND_GLOBALMUTE_DISABLE.getMessage(sender).replace(
                    "%player%", AdvancedLobby.getName(
                        sender
                    )
                )
            )
        }
        return true
    }
}