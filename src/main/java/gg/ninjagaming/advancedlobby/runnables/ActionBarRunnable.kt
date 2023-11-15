package gg.ninjagaming.advancedlobby.runnables

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.titleapi.TitleApi.sendActionBar
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

object ActionBarRunnable {
    fun schedulingRunnable(messages: ArrayList<String>, displayTime: Int): Runnable {
        val r = Runnable {
            for (player in Bukkit.getOnlinePlayers())
            {
                if (AdvancedLobby.lobbyWorlds.contains(player.world))
                {
                    handleOnlinePlayer(player, messages, displayTime)
                }

            }

        }
        return r
        }

    }


    private fun handleOnlinePlayer(player: Player, messages: ArrayList<String>, displayTime: Int)
    {
        var delay = 0L
        messages.forEach()
        {
            SelfCancelingRunnable(player, it,displayTime).runTaskTimer(AdvancedLobby.getInstance(), delay,0)
            delay += displayTime * 20L
        }

    }

class SelfCancelingRunnable(private val player: Player, private val message: String, displayTime: Int) : BukkitRunnable()
{
    private var counter = 20*displayTime

    override fun run() {
        if (counter <= 0) {
            this.cancel()
            return
        }

        counter--

        var actionbarMessage = translateAlternateColorCodes('&', message.replace("%player_ping%", player.ping.toString()))

        if (AdvancedLobby.placeholderApi)
            actionbarMessage =  PlaceholderAPI.setPlaceholders(player, message)



        sendActionBar(player, actionbarMessage)
    }
}
