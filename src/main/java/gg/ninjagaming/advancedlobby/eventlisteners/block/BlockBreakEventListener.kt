package gg.ninjagaming.advancedlobby.eventlisteners.block

import gg.ninjagaming.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakEventListener : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (AdvancedLobby.multiWorld_mode or !AdvancedLobby.lobbyWorlds.contains(player.world))
            return

        if (AdvancedLobby.build.contains(player))
            return

        event.isCancelled = true
    }
}
