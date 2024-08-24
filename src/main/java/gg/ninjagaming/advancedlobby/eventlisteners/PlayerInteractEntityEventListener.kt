package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class PlayerInteractEntityEventListener : Listener {
    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        if (AdvancedLobby.multiWorld_mode or ! AdvancedLobby.lobbyWorlds.contains(player.world))
            return

        if (event.rightClicked.type != EntityType.ITEM_FRAME && AdvancedLobby.build.contains(player))
            return

        if (player.inventory.itemInMainHand.type != Material.NAME_TAG && AdvancedLobby.build.contains(player))
            return

        event.isCancelled = true
    }
}
