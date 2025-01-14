package gg.ninjagaming.advancedlobby.eventlisteners.entity

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import org.bukkit.event.entity.EntitySpawnEvent

object EntitySpawnEventListener: Listener {
    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        if (event.entity.entitySpawnReason != SpawnReason.CUSTOM || event.entity !is Player){
            event.isCancelled = true
        }

    }
}