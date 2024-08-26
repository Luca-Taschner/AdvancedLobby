package gg.ninjagaming.advancedlobby.commands

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameModeCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command is available for players only.")
            return true
        }

        if (!sender.hasPermission("advancedlobby.commands.gamemode")) {
            sender.sendMessage(Locale.NO_PERMISSION.getMessage(sender))
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(Locale.COMMAND_GAMEMODE_USAGE.getMessage(sender))
            return true
        }

        if (args.size == 1) {
            updateSenderGameMode(args, sender)
            return true
        }

        if (args.size == 2) {
            updateOtherPlayerGamemode(args, sender)
            return true
        }

        sender.sendMessage(Locale.COMMAND_GAMEMODE_USAGE.getMessage(sender))
        return true
    }

    private fun updateOtherPlayerGamemode(args: Array<String>, sender: Player) {
        val target = Bukkit.getPlayerExact(args[1])

        if (target == null) {
            sender.sendMessage(
                Locale.PLAYER_NOT_FOUND.getMessage(sender).replace(
                    "%player%",
                    args[1]
                )
            )
            return
        }

        val gameMode: GameMode?
        val mode = args[0]

        gameMode = when (mode) {
            ("1") -> GameMode.CREATIVE
            ("2") -> GameMode.ADVENTURE
            ("3") -> GameMode.SPECTATOR
            else -> null
        }

        if (gameMode == null) {
            sender.sendMessage(Locale.COMMAND_GAMEMODE_USAGE.getMessage(sender))
            return
        }

        if (target === sender) {
            sender.gameMode = gameMode
            sender.sendMessage(
                Locale.COMMAND_GAMEMODE_SWITCH.getMessage(sender).replace("%gamemode%", gameMode.name)
            )
            return
        }

        target.gameMode = gameMode
        target.sendMessage(
            Locale.COMMAND_GAMEMODE_SWITCH.getMessage(sender).replace("%gamemode%", gameMode.name)
        )

        sender.sendMessage(
            Locale.COMMAND_GAMEMODE_SWITCH_OTHER.getMessage(sender).replace("%gamemode%", gameMode.name)
                .replace("%player%", AdvancedLobby.getName(target))
        )
        return
    }

    private fun updateSenderGameMode(args: Array<String>, sender: Player) {
        val gameMode: GameMode?
        val mode = args[0].lowercase(java.util.Locale.getDefault())

        gameMode = when (mode) {
            ("1"), ("creative") -> GameMode.CREATIVE
            ("2"), ("adventure") -> GameMode.ADVENTURE
            ("3"), ("spectator") -> GameMode.SPECTATOR
            else -> null
        }
        if (gameMode == null) {
            sender.sendMessage(Locale.COMMAND_GAMEMODE_USAGE.getMessage(sender))
            return
        }
        sender.gameMode = gameMode
        sender.sendMessage(Locale.COMMAND_GAMEMODE_SWITCH.getMessage(sender).replace("%gamemode%", gameMode.name))
        return
    }
}