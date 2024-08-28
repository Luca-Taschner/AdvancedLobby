package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class PlayerSwapHandItemsEventListener : Listener {
    @EventHandler
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        if (!(!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(player.world)))
            return

        if (AdvancedLobby.build.contains(player))
            return

        event.isCancelled = true


    }
}
