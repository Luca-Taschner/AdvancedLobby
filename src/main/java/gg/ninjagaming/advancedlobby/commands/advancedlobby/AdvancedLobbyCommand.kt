package gg.ninjagaming.advancedlobby.commands.advancedlobby

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.inventorybuilder.*
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.entity.Player
import java.io.IOException

class AdvancedLobbyCommand : CommandExecutor {
    private val prefix = "§8┃ §bAdvancedLobby §8┃ "

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            if (!sender.hasPermission("advancedlobby.admin"))
                return true

            if (args.size != 1)
                return true

            if (args[0].equals("reload", ignoreCase = true) or args[0].equals("rl", ignoreCase = true) && sender.hasPermission("advancedlobby.reload"))
                reloadServer(sender)


            if (args[0].equals("errors", ignoreCase = true))
                this.sendErrors(sender)

            return true
        }

        if (!sender.hasPermission("advancedlobby.admin")) {
            sender.sendMessage(Locale.NO_PERMISSION.getMessage(sender))
            return true
        }

        if (args.isEmpty()) {
            sendPluginHelp(sender)
            return true
        }

        when(args[0].lowercase()) {
            "reload", "rl" -> {
                reloadServer(sender)
                return true
            }

            "errors" -> {
                this.sendErrors(sender)
                return true
            }

            "location", "loc" -> {
                if (!handleLocationArgument(sender, args, prefix))
                    sendPluginHelp(sender)
            }

            else -> {
                sendPluginHelp(sender)
                return true
            }
        }
        return true
    }

    private fun reloadServer(sender: CommandSender) {
        val start = System.currentTimeMillis()
        sender.sendMessage("")
        sender.sendMessage(this.prefix + "§cReloading§8..")
        try {
            if (!AdvancedLobby.file.exists()) {
                AdvancedLobby.getInstance().saveDefaultConfig()
            }
            if (!AdvancedLobby.fileLocations.exists() or !AdvancedLobby.fileMessages.exists() or !AdvancedLobby.fileSounds.exists()) {
                AdvancedLobby.getInstance().logger.info("One or more files were not found. Creating..")
                if (!AdvancedLobby.fileLocations.exists()) {
                    AdvancedLobby.fileLocations.parentFile.mkdirs()
                    try {
                        AdvancedLobby.fileLocations.createNewFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (!AdvancedLobby.fileMessages.exists()) {
                    AdvancedLobby.fileMessages.parentFile.mkdirs()
                    AdvancedLobby.getInstance().saveResource("messages.yml", false)
                }
                if (!AdvancedLobby.fileSounds.exists()) {
                    AdvancedLobby.fileMessages.parentFile.mkdirs()
                    AdvancedLobby.getInstance().saveResource("sounds.yml", false)
                }
            }

            AdvancedLobby.cfg.load(AdvancedLobby.file)
            AdvancedLobby.cfgM.load(AdvancedLobby.fileMessages)
            AdvancedLobby.cfgL.load(AdvancedLobby.fileLocations)
            AdvancedLobby.cfgS.load(AdvancedLobby.fileSounds)

            AdvancedLobby.actionbarMessages.clear()
            if (AdvancedLobby.cfg.getBoolean("actionbar.enabled") && AdvancedLobby.actionBarRunnable != null) {
                AdvancedLobby.actionBarRunnable.cancel()
                AdvancedLobby.actionbarMessages.addAll(AdvancedLobby.cfg.getStringList("actionbar.messages"))
                AdvancedLobby.prepareActionBarRunnable()
            }

            CompassInventory.updateInventory()
            CosmeticsInventory.updateInventory()
            CosmeticsGadgetsInventory.updateInventory()
            CosmeticsBalloonsInventory.updateInventory()
            CosmeticsParticlesInventory.updateInventory()
            CosmeticsHatsInventory.updateInventory()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
        val duration = System.currentTimeMillis() - start
        sender.sendMessage(this.prefix + "§aReload finished, took §e" + duration + "ms§8.")
        sender.sendMessage("")
    }

    private fun sendErrors(sender: CommandSender) {
        sender.sendMessage("")
        sender.sendMessage("§8┃ §4● §8┃ §cErrors §8× §7Total§8: §f" + AdvancedLobby.errors.size)
        sender.sendMessage("§8┃ §4● §8┃ ")

        if (AdvancedLobby.errors.isEmpty()) {
            sender.sendMessage("§8┃ §4● §8┃ §8- §aThere are no errors§8.")
        }

        for (error in AdvancedLobby.errors.keys) {
            sender.sendMessage("§8┃ §4● §8┃ §8- §7Couldn't find §f" + AdvancedLobby.errors[error] + "§8: §c" + error)
        }
        sender.sendMessage("")
    }

    private fun sendPluginHelp(sender: CommandSender) {
        sender.sendMessage("")
        sender.sendMessage(
            ("§8┃ §b● §8┃ §bAdvancedLobby §8× §av"
                    + AdvancedLobby.getInstance().description.version + " §7by cyne & NinjaGaming")
        )
        sender.sendMessage("§8┃ §b● §8┃ ")
        sender.sendMessage("§8┃ §b● §8┃ §8/§fadvancedlobby reload §8- §7Reload the configuration files")
        sender.sendMessage("§8┃ §b● §8┃ §8/§fadvancedlobby location §8- §7Manage the locations")
        sender.sendMessage("§8┃ §b● §8┃ §8/§fadvancedlobby errors §8- §7List all errors")
        sender.sendMessage("")
    }
}