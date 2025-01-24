package gg.ninjagaming.advancedlobby.eventlisteners.entity

import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import org.bukkit.event.entity.EntitySpawnEvent

object EntitySpawnEventListener: Listener {
    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        val entity = event.entity

        if (entity.entitySpawnReason == SpawnReason.CUSTOM)
            return

        if (entity.type == EntityType.FISHING_BOBBER)
            return

        if (entity !is Player){
            event.isCancelled = true
            return
        }
    }
}