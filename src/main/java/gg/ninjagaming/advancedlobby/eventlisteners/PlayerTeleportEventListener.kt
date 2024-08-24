package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.cosmetics.Cosmetics
import gg.ninjagaming.advancedlobby.runnables.PlayerTeleportBalloonRunnable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerTeleportEventListener :Listener {
    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val player: Player = event.player

        if (!Cosmetics.balloons.containsKey(player))
            return


        Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.getInstance(), PlayerTeleportBalloonRunnable.schedulingRunnable(player), 2L)
    }
}