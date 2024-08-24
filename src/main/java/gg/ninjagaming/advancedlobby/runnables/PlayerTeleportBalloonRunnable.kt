package gg.ninjagaming.advancedlobby.runnables


import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.cosmetics.Cosmetics
import gg.ninjagaming.advancedlobby.titleapi.TitleApi.sendTabTitle
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
