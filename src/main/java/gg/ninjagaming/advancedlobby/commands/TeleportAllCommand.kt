package gg.ninjagaming.advancedlobby.commands

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TeleportAllCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command is available for players only.")
            return true
        }

        if (!sender.hasPermission("advancedlobby.commands.teleportall")) {
            sender.sendMessage(Locale.NO_PERMISSION.getMessage(sender))
            return true
        }

        for (players in Bukkit.getOnlinePlayers()) {
            if (players === sender)
                continue

            AdvancedLobby.playSound(sender, sender.location, "commands.teleportall_command")

            if (AdvancedLobby.silentLobby.contains(players))
                continue

            players.teleport(sender)
            AdvancedLobby.playSound(players, sender.location, "commands.teleportall_command")
            players.sendMessage(
                Locale.COMMAND_TELEPORTALL_TELEPORT.getMessage(sender)
                    .replace("%player%", AdvancedLobby.getName(sender))
            )
        }

        sender.sendMessage(
            Locale.COMMAND_TELEPORTALL_TELEPORT.getMessage(sender)
                .replace("%player%", AdvancedLobby.getName(sender))
        )
        return true
    }
}
