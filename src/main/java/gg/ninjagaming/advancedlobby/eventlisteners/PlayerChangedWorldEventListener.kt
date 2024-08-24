package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.cosmetics.Cosmetics
import gg.ninjagaming.advancedlobby.misc.EnterLobbyHelper
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangedWorldEventListener : Listener {
    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        val player = event.player

        if (!AdvancedLobby.multiWorld_mode)
            return

        if (AdvancedLobby.lobbyWorlds.contains(player.world))
            playerEnterLobby(player)


        if (AdvancedLobby.lobbyWorlds.contains(event.from) && !AdvancedLobby.lobbyWorlds.contains(player.world))
            playerLeaveLobby(player)

    }

    private fun playerEnterLobby(player: Player) {
        EnterLobbyHelper.setPlayerValues(player)

        for (effects in player.activePotionEffects) {
            player.removePotionEffect(effects.type)
        }

        EnterLobbyHelper.setPlayerGamemode(player)

        if (AdvancedLobby.cfg.getBoolean("multiworld_mode.clear_inventory"))
            EnterLobbyHelper.clearPlayerInventory(player)


        if (AdvancedLobby.cfg.getBoolean("title.enabled"))
            EnterLobbyHelper.sendPlayerTitle(player)


        EnterLobbyHelper.setPlayerHotbarItems(player)

        EnterLobbyHelper.hideHiddenPlayers(player)

        EnterLobbyHelper.hideSilentLobbyPlayers(player)

        if (AdvancedLobby.cfg.getBoolean("player_join.join_at_spawn"))
            EnterLobbyHelper.joinAtSpawn(player)

        if (AdvancedLobby.updateAvailable && player.hasPermission("advancedlobby.admin"))
            EnterLobbyHelper.displayAdminMessage(player)
    }

    private fun playerLeaveLobby(player: Player) {
        if (Cosmetics.balloons.containsKey(player)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                AdvancedLobby.getInstance(),
                { Cosmetics.balloons[player]!!.remove() }, 5L
            )
        }
        AdvancedLobby.build.remove(player)
        AdvancedLobby.buildInventory.remove(player)
        AdvancedLobby.fly.remove(player)
        AdvancedLobby.playerHider.remove(player)
        AdvancedLobby.shield.remove(player)
        AdvancedLobby.silentLobby.remove(player)

        Cosmetics.hats.remove(player)
        Cosmetics.gadgets.remove(player)

        for (players in Bukkit.getOnlinePlayers()) {
            if (!AdvancedLobby.silentLobby.contains(players)) {
                player.showPlayer(AdvancedLobby.getInstance(), players)
            }
        }

        if (AdvancedLobby.cfg.getBoolean("multiworld_mode.clear_inventory")) {
            EnterLobbyHelper.clearPlayerInventory(player)
        }
    }
}
