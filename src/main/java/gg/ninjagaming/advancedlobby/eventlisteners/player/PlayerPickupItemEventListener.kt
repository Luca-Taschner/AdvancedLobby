package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import io.papermc.paper.event.player.PlayerPickItemEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerPickupItemEventListener : Listener {
    @EventHandler
    fun onPlayerPickupItem(event: PlayerPickItemEvent) {
        val player = event.player


        if (!(!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(player.world)))
            return

        if (!AdvancedLobby.build.contains(player))
            event.isCancelled = true

    }
}
