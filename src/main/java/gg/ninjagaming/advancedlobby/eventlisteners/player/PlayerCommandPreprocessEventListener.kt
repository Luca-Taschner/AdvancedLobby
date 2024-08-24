package gg.ninjagaming.advancedlobby.eventlisteners.player

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.locale.Locale
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerCommandPreprocessEventListener : Listener {
    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val message = event.message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

        val helpTopic = Bukkit.getHelpMap().getHelpTopic(message)

        if (helpTopic == null) {
            event.isCancelled = true
            player.sendMessage(Locale.UNKNOWN_COMMAND.getMessage(player).replace("%command%", message))
            return
        }

        if (!AdvancedLobby.cfg.getStringList("block_commands.commands").contains(message) || player.hasPermission("advancedlobby.admin"))
            return

        if (!AdvancedLobby.cfg.getBoolean("block_commands.enabled"))
            return

        event.isCancelled = true
        player.sendMessage(Locale.NO_PERMISSION.getMessage(player))
    }
}
