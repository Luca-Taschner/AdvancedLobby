package gg.ninjagaming.advancedlobby.eventlisteners.entity

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

class FoodLevelChangeEventListener : Listener {
    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (event.entity !is Player)
            return

        val player = event.entity as Player
        if (!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(player.world)) {
            event.isCancelled = true
        }
    }
}
