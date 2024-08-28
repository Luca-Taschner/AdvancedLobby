package gg.ninjagaming.advancedlobby.commands

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BuildCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command is available for players only.")
            return true
        }

        if (!sender.hasPermission("advancedlobby.commands.build")) {
            sender.sendMessage(Locale.NO_PERMISSION.getMessage(sender))
            return true
        }

        if (!AdvancedLobby.build.contains(sender)) {
            AdvancedLobby.build.add(sender)
            sender.sendMessage(Locale.COMMAND_BUILD_JOIN.getMessage(sender))

            sender.gameMode = GameMode.CREATIVE

            AdvancedLobby.buildInventory[sender] = sender.inventory.contents as Array<ItemStack>
            sender.inventory.clear()
            return true
        }
        AdvancedLobby.build.remove(sender)
        sender.sendMessage(Locale.COMMAND_BUILD_LEAVE.getMessage(sender))

        val gameMode: GameMode
        val mode = AdvancedLobby.cfg.getString("player_join.gamemode")

        gameMode = when (mode) {
            ("1") -> GameMode.CREATIVE
            ("2") -> GameMode.ADVENTURE
            ("3") -> GameMode.SPECTATOR
            else -> GameMode.SURVIVAL
        }

        sender.gameMode = gameMode

        sender.inventory.clear()
        sender.inventory.contents = AdvancedLobby.buildInventory[sender]!!
        return true
    }
}