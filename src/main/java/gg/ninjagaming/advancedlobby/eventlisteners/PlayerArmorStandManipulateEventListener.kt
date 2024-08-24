package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerArmorStandManipulateEvent

class PlayerArmorStandManipulateEventListener : Listener {
    @EventHandler
    fun onPlayerArmorStandManipulate(event: PlayerArmorStandManipulateEvent) {
        val player = event.player
        if ((AdvancedLobby.multiWorld_mode or !AdvancedLobby.lobbyWorlds.contains(player.world)))
            return

        if (AdvancedLobby.build.contains(player))
            return

        event.isCancelled = true
    }
}
