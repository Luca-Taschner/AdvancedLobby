package gg.ninjagaming.advancedlobby.runnables

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.titleapi.TitleApi.sendActionBar
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

object ActionBarRunnable {
    fun schedulingRunnable(messages: ArrayList<String>, displayTime: Int): Runnable {
        val r = Runnable {
            handleMessages(messages,displayTime)
        }
        return r
    }

    private fun handleMessages(messages: ArrayList<String>, displayTime: Int)
    {
        var delay = 0L
        messages.forEach()
        {
            SelfCancelingRunnable(it,displayTime).runTaskTimer(AdvancedLobby.instance!!, delay,0)
            delay += displayTime * 20L
        }
    }
}

class SelfCancelingRunnable(private val message: String, displayTime: Int) : BukkitRunnable()
{
    private var counter = 20*displayTime

    override fun run() {
        if (counter <= 0) {
            this.cancel()
            return
        }

        counter--

        for (player in Bukkit.getOnlinePlayers())
        {
            if (!AdvancedLobby.lobbyWorlds.contains(player.world))
                continue

            var actionbarMessage = message.replace("%player_ping%", player.ping.toString())

            if (AdvancedLobby.placeholderApi)
                actionbarMessage =  PlaceholderAPI.setPlaceholders(player, message)

            sendActionBar(player, actionbarMessage)
        }
    }
}