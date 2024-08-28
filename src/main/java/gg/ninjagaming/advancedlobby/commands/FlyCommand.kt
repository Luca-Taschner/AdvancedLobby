package gg.ninjagaming.advancedlobby.commands

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FlyCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command is available for players only.")
            return true
        }

        if (!sender.hasPermission("advancedlobby.commands.fly")) {
            sender.sendMessage(Locale.NO_PERMISSION.getMessage(sender))
            return true
        }

        if (args.isEmpty()) {
            if (updateFlightForPlayer(sender)){
                sender.sendMessage(Locale.COMMAND_FLY_ENABLE.getMessage(sender))
                return true
            }

            sender.sendMessage(Locale.COMMAND_FLY_DISABLE.getMessage(sender))
            return true
        }

        if (args.size == 1) {
            val target = Bukkit.getPlayerExact(args[0])
            if (target == null) {
                sender.sendMessage(Locale.PLAYER_NOT_FOUND.getMessage(sender).replace("%player%", args[0]))
                return true
            }

            if (target === sender) {

                if (updateFlightForPlayer(sender)) {
                    sender.sendMessage(Locale.COMMAND_FLY_ENABLE.getMessage(sender))
                    return true
                }
                sender.sendMessage(Locale.COMMAND_FLY_DISABLE.getMessage(sender))
                return true

            }

            if (updateFlightForPlayer(target)){
                target.sendMessage(Locale.COMMAND_FLY_ENABLE.getMessage(sender))
                sender.sendMessage(Locale.COMMAND_FLY_ENABLE_OTHER.getMessage(sender).replace("%player%", AdvancedLobby.getName(target)))
                return true
            }

            target.sendMessage(Locale.COMMAND_FLY_DISABLE.getMessage(sender))
            sender.sendMessage(Locale.COMMAND_FLY_DISABLE_OTHER.getMessage(sender).replace("%player%", AdvancedLobby.getName(target)))
            return true

        }
        sender.sendMessage(Locale.COMMAND_FLY_USAGE.getMessage(sender))
        return true
    }

    /**
     * Updates the flight status for a player.
     *
     * @param target The player to update the flight status for.
     * @return True if the flight status was updated to enabled, false if it was updated to disable.
     */
    private fun updateFlightForPlayer(target: Player): Boolean
    {
        if (!AdvancedLobby.fly.contains(target)) {
            AdvancedLobby.fly.add(target)
            target.allowFlight = true
            target.isFlying = true

            return true
        }
        AdvancedLobby.fly.remove(target)
        target.allowFlight = false
        target.isFlying = false

        return false
    }

}
