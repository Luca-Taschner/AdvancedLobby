package gg.ninjagaming.advancedlobby.eventlisteners.entity

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent

class EntityExplodeEventListener : Listener {
    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (AdvancedLobby.multiWorld_mode or !AdvancedLobby.lobbyWorlds.contains(event.entity.world))
            return

        event.isCancelled = true
        event.blockList().clear()
    }
}
