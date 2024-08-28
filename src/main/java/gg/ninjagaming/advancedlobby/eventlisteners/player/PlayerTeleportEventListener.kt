package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.extras.cosmetics.Cosmetics
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


        Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.instance!!, PlayerTeleportBalloonRunnable.schedulingRunnable(player), 2L)
    }
}