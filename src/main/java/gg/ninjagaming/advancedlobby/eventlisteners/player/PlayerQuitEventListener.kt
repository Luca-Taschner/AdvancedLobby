package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.extras.cosmetics.Cosmetics
import gg.ninjagaming.advancedlobby.misc.Locale
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitEventListener : Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        val newQuitMessage = Locale.QUIT_MESSAGE.getMessage(player).replace("%player%", AdvancedLobby.getName(player))
        event.quitMessage(Component.text(newQuitMessage))


        if (Cosmetics.balloons.containsKey(player)) {
            Cosmetics.balloons[player]!!.removeBalloon()
        }

        AdvancedLobby.build.remove(player)
        AdvancedLobby.buildInventory.remove(player)
        AdvancedLobby.fly.remove(player)
        AdvancedLobby.playerHider.remove(player)
        AdvancedLobby.shield.remove(player)
        AdvancedLobby.silentLobby.remove(player)
    }
}
