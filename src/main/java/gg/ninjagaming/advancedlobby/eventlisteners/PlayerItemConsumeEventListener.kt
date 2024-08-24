package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent

class PlayerItemConsumeEventListener : Listener {
    @EventHandler
    fun onPlayerItemConsume(event: PlayerItemConsumeEvent) {
        val player = event.player
        if (!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(player.world)) {
            event.isCancelled = true
        }
    }
}
