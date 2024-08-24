package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.LeavesDecayEvent

class LeavesDecayEventListener : Listener {
    @EventHandler
    fun onLeavesDecay(event: LeavesDecayEvent) {
        if (AdvancedLobby.multiWorld_mode or !AdvancedLobby.lobbyWorlds.contains(event.block.world))
            return

        event.isCancelled = true
    }
}
