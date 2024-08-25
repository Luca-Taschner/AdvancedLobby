package gg.ninjagaming.advancedlobby.commands

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.locale.Locale
import de.cyne.advancedlobby.misc.LocationManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LobbyCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command is available for players only.")
            return true
        }

        if (AdvancedLobby.lobbyWorlds.contains(sender.world)) {
            sender.sendMessage(Locale.COMMAND_LOBBY_ALREADY_IN_LOBBY.getMessage(sender))
            return true
        }

        val location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"))
        if (location != null) {
            sender.teleport(location)
        }

        sender.sendMessage(Locale.COMMAND_LOBBY_TELEPORT.getMessage(sender))
        return true
    }
}