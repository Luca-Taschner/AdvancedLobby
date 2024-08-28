package gg.ninjagaming.advancedlobby.commands.advancedlobby

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.LocationManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun handleLocationArgument(player: Player, args: Array<out String>, prefix: String) : Boolean {
    when (args.size) {
        1 -> {
            //No additional args provided
            player.sendMessage("$prefix§cUsage§8: /§cadvancedlobby location §8<§clist§8, §cset§8, §cremove§8, §cteleport§8> [§clocation-name§8]")
            return true
        }

        2 ->{
            if (args[1].equals("list", ignoreCase = true)) {
                listLocations(player)
                return true
            }

            player.sendMessage(displayTwoArgumentsHelp(args[1]))
            return true
        }

        3 -> {
            if (args[1].equals("set", ignoreCase = true)) {
                LocationManager.saveLocation(player.location, args[2])
                player.sendMessage(prefix + "§7The location §b" + args[2] + " §7has been set §asuccessfully§8.")
                return true
            }

            if (args[1].equals("remove", ignoreCase = true)) {
                if (LocationManager.getLocation(args[2]) != null) {
                    LocationManager.deleteLocation(args[2])
                    player.sendMessage(prefix + "§7The location §b" + args[2] + " §7has been §cremoved§8.")
                    return true
                }
                player.sendMessage(prefix + "§cThe location §b" + args[2] + " §cwas not found§8.")
                return true
            }

            if (args[1].equals("teleport", ignoreCase = true) or args[1].equals("tp", ignoreCase = true)) {
                val teleportLocation = LocationManager.getLocation(args[2])
                if (teleportLocation != null) {
                    player.teleport(teleportLocation)
                    player.sendMessage(prefix + "§7You have been teleported to §b" + args[2] + "§8.")
                    return true
                }
                player.sendMessage(prefix + "§cThe location §b" + args[2] + " §cwas not found§8.")
                return true
            }

            player.sendMessage("$prefix§cUsage§8: /§cadvancedlobby location §8<§clist§8, §cset§8, §cremove§8, §cteleport§8> [§clocation-name§8]")
            return true

        }

        else -> return false

    }
}

private fun listLocations(sender: CommandSender){
    val locations = AdvancedLobby.cfgL.getKeys(false)
    sender.sendMessage("")
    sender.sendMessage("§8┃ §b● §8┃ §bLocations §8× §7Total§8: §f" + locations.size)
    sender.sendMessage("§8┃ §b● §8┃ ")

    if (locations.isEmpty()) {
        sender.sendMessage("§8┃ §b● §8┃ §8- §cThere are no saved locations§8.")
        sender.sendMessage("§8┃ §b● §8┃ §8- §7Use §8/§eadvancedlobby location set §8<§elocation-name§8> §7")
        sender.sendMessage("§8┃ §b● §8┃ §7to add a new location.")
    }

    for (location in locations) {
        val component = Component.text("§8┃ §b● §8┃ §8- §f$location")
        val spacer = Component.text(" §8┃ ")

        val teleportComponent = Component.text("§aTeleport")
            .hoverEvent(HoverEvent.showText(Component.text("§8► §7Click to teleport to §8'§b$location§8'")))
            .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/advancedlobby location teleport $location"))

        val removeComponent = Component.text("§cRemove")
            .hoverEvent(HoverEvent.showText(Component.text("§8► §7Click to remove §8'§b$location§8'")))
            .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/advancedlobby location remove $location"))

        val finalComponent = component
            .append(spacer)
            .append(teleportComponent)
            .append(spacer)
            .append(removeComponent)

        sender.sendMessage(finalComponent)
    }
    sender.sendMessage("")
    return
}

private fun displayTwoArgumentsHelp(argument: String) :String
{
    return when (argument) {
        "set" -> "§cUsage§8: /§cadvancedlobby location set §8<§clocation-name§8>"

        "remove" -> "§cUsage§8: /§cadvancedlobby location remove §8<§clocation-name§8>"

        "teleport" -> "§cUsage§8: /§cadvancedlobby location teleport §8<§clocation-name§8>"

        else -> "§cUsage§8: /§cadvancedlobby location §8<§clist§8, §cset§8, §cremove§8, §cteleport§8> [§clocation-name§8]"
    }
}