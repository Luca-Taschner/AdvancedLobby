package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerUnleashEntityEvent

class PlayerUnleashEntityEventListener :Listener{
    @EventHandler
    fun onPlayerUnleashEntityEvent(event: PlayerUnleashEntityEvent) {
        val player: Player = event.player

        if (AdvancedLobby.multiWorld_mode && !AdvancedLobby.lobbyWorlds.contains(player.world)) {
            return
        }

        if (AdvancedLobby.build.contains(player)) {
            return
        }

        event.isCancelled = true

    }
}