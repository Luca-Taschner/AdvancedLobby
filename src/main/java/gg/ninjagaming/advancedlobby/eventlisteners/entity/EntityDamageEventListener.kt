package gg.ninjagaming.advancedlobby.eventlisteners.entity

import gg.ninjagaming.advancedlobby.AdvancedLobby
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EntityDamageEventListener : Listener {
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            handlePlayerDamage(event)
            return
        }

        if (AdvancedLobby.cfg.getBoolean("disable_mob_damage")) {
            handleMobDamage(event)
        }
    }

    private fun handleMobDamage(event: EntityDamageEvent) {
        if (!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(event.entity.world)) {
            event.isCancelled = true
        }
    }

    private fun handlePlayerDamage(event: EntityDamageEvent) {
        val player = event.entity as Player
        if (!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(player.world)) {
            event.isCancelled = true
        }
    }
}
