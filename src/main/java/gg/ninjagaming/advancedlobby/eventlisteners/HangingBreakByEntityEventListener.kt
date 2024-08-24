package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.hanging.HangingBreakByEntityEvent

class HangingBreakByEntityEventListener : Listener {
    @EventHandler
    fun onHangingBreakByEntity(event: HangingBreakByEntityEvent) {
        if (event.remover !is Player)
            return

        val player = event.remover as Player
        if (!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(player.world)) {
            event.isCancelled = true
        }
    }
}
