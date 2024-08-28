package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.EnterLobbyHelper
import gg.ninjagaming.advancedlobby.misc.Locale
import gg.ninjagaming.advancedlobby.misc.LocationManager
import gg.ninjagaming.advancedlobby.runnables.PlayerTabListRunnable
import gg.ninjagaming.advancedlobby.titleapi.TitleApi.sendTabTitle
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEventListener :Listener{
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        val joinMessage = Locale.JOIN_MESSAGE.getMessage(player).replace("%player%", player.name)
        event.joinMessage(Component.text(joinMessage))

        EnterLobbyHelper.setPlayerValues(player)

        for(potionEffect in player.activePotionEffects){
            player.removePotionEffect(potionEffect.type)
        }

        EnterLobbyHelper.setPlayerGamemode(player)

        if (AdvancedLobby.cfg.getBoolean("player_join.clear_inventory"))
            EnterLobbyHelper.clearPlayerInventory(player)

        if (AdvancedLobby.cfg.getBoolean("title.enabled"))
            EnterLobbyHelper.sendPlayerTitle(player)

        if (AdvancedLobby.cfg.getBoolean("tablist_header_footer.enabled"))
        enableTabList(player)

        EnterLobbyHelper.setPlayerHotbarItems(player)

        EnterLobbyHelper.hideHiddenPlayers(player)

        EnterLobbyHelper.hideSilentLobbyPlayers(player)

        if (!player.hasPlayedBefore())
            fistTimeJoin(player)

        if (AdvancedLobby.cfg.getBoolean("player_join.join_at_spawn"))
            EnterLobbyHelper.joinAtSpawn(player)

        if(player.hasPermission("advancedlobby.admin"))
            EnterLobbyHelper.displayAdminMessage(player)

    }

    /**
     * Enables the tab list for the specified player.
     *
     * @param player the player for whom to enable the tab list
     */
    private fun enableTabList(player: Player){
        if (AdvancedLobby.cfg.getBoolean("tablist_header_footer.update_tablist"))
            addTabListScheduler(player)

        if (AdvancedLobby.placeholderApi) {
            sendTabTitle(
                player, AdvancedLobby.getPlaceholderString(player, "tablist_header_footer.header"),
                AdvancedLobby.getPlaceholderString(player, "tablist_header_footer.footer")
            )
            return
        }

        sendTabTitle(
            player, AdvancedLobby.getString("tablist_header_footer.header"),
            AdvancedLobby.getString("tablist_header_footer.footer")
        )

    }

    /**
     * Adds a scheduler to update the tab list for the specified player.
     *
     * @param player the player for whom to add the tab list scheduler
     */
    private fun addTabListScheduler(player: Player){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvancedLobby.instance!!,PlayerTabListRunnable.schedulingRunnable(player),0L,20L)
    }

    private fun fistTimeJoin(player: Player){
        val location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"))
        if (location != null) {
            player.teleport(location)
        }
    }

    
}