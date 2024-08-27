package gg.ninjagaming.advancedlobby.runnables


import gg.ninjagaming.advancedlobby.misc.extras.cosmetics.Cosmetics
import org.bukkit.entity.Player

object PlayerTeleportBalloonRunnable {
    fun schedulingRunnable(player:  Player): Runnable {
        val r = Runnable {
            Cosmetics.balloons[player]!!.remove()
            Cosmetics.balloons[player]!!.create()
        }
        return r
    }
}
