package gg.ninjagaming.advancedlobby.eventlisteners.block

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceEventListener : Listener {
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (AdvancedLobby.multiWorld_mode or !AdvancedLobby.lobbyWorlds.contains(player.world))
            return

        if (AdvancedLobby.build.contains(player))
            return

        event.isCancelled = true
    }
}
