package gg.ninjagaming.advancedlobby.eventlisteners.player

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent

class PlayerItemHeldEventListener : Listener {
    @EventHandler
    fun onPlayerItemHeld(e: PlayerItemHeldEvent) {
        val p = e.player
        if (!(!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(p.world)))
            return

        if (AdvancedLobby.build.contains(p))
            return

        AdvancedLobby.playSound(p, p.location, "hotbar_switch")
    }
}
