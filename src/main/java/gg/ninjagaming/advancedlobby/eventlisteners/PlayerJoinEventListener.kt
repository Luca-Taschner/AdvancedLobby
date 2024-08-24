package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.locale.Locale
import de.cyne.advancedlobby.misc.HiderType
import de.cyne.advancedlobby.misc.LocationManager
import gg.ninjagaming.advancedlobby.itembuilders.PlayerHotbarItemBuilder
import gg.ninjagaming.advancedlobby.runnables.PlayerTabListRunnable
import gg.ninjagaming.advancedlobby.titleapi.TitleApi.sendTabTitle
import gg.ninjagaming.advancedlobby.titleapi.TitleApi.sendTitle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
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

        setPlayerValues(player)

        for(potionEffect in player.activePotionEffects){
            player.removePotionEffect(potionEffect.type)
        }

        setPlayerGamemode(player)

        if (AdvancedLobby.cfg.getBoolean("player_join.clear_inventory"))
            clearPlayerInventory(player)

        if (AdvancedLobby.cfg.getBoolean("title.enabled"))
            sendPlayerTitle(player)

        if (AdvancedLobby.cfg.getBoolean("tablist_header_footer.enabled"))
        enableTabList(player)

        setPlayerHotbarItems(player)

        hideHiddenPlayers(player)

        hideSilentLobbyPlayers(player)

        if (!player.hasPlayedBefore())
            fistTimeJoin(player)

        if (AdvancedLobby.cfg.getBoolean("player_join.join_at_spawn"))
            joinAtSpawn(player)

        if(player.hasPermission("advancedlobby.admin"))
            displayAdminMessage(player)

    }

    /**
     * Sets the values of a player when they join the lobby.
     *
     * @param player the player whose values will be set
     */
    private fun setPlayerValues(player: Player){
        player.health = AdvancedLobby.cfg.getDouble("player_join.health")
        player.foodLevel = 20

        player.allowFlight = false
        player.isFlying = false

        player.fireTicks = 0

        player.level = 0
        player.exp = 0f
    }

    /**
     * Sets the player's game mode based on the value specified in the configuration file when they join the lobby.
     * @param player The player whose game mode will be set.
     */
    private fun setPlayerGamemode(player: Player){
        val gamemodeFromConfig = AdvancedLobby.cfg.getString("player_join.gamemode")

        val gameMode: GameMode = when(gamemodeFromConfig){
            "1" -> GameMode.CREATIVE
            "2" -> GameMode.ADVENTURE
            "3" -> GameMode.SPECTATOR
            else -> GameMode.SURVIVAL
        }

        player.gameMode = gameMode
    }

    /**
     * Clears the inventory of a player when they join the lobby.
     *
     * @param player the player whose inventory will be cleared
     */
    private fun clearPlayerInventory(player: Player){
        val inventory = player.inventory
        inventory.clear()
        inventory.setArmorContents(null)
    }

    /**
     * Sends a title to a player when they join the lobby.
     *
     * @param player the player to send the title to
     */
    private fun sendPlayerTitle(player: Player){
        if (AdvancedLobby.placeholderApi) {
            sendTitle(player,2,6,2,
                AdvancedLobby.getPlaceholderString(player, "title.title").replace("%player%", AdvancedLobby.getName(player)),
                AdvancedLobby.getPlaceholderString(player, "title.subtitle").replace("%player%", AdvancedLobby.getName(player))
            )

            return
        }

        sendTitle(player,2, 6, 2,
            AdvancedLobby.getString("title.title").replace("%player%", AdvancedLobby.getName(player)),
            AdvancedLobby.getString("title.subtitle").replace("%player%", AdvancedLobby.getName(player))
        )
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvancedLobby.getInstance(),PlayerTabListRunnable.schedulingRunnable(player),0L,20L)
    }

    /**
     * Sets the player's hotbar items based on the values specified in the configuration file when they join the lobby.
     *
     * @param player the player whose hotbar items will be set
     */
    private fun setPlayerHotbarItems(player: Player){
        if (AdvancedLobby.cfg.getBoolean("hotbar_items.teleporter.enabled")) {
            val teleporterItem = PlayerHotbarItemBuilder.teleporterItemBuilder
            player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.teleporter.slot"), teleporterItem)
        }

        if (AdvancedLobby.cfg.getBoolean("hotbar_items.player_hider.enabled")) {
            val hiderItem = PlayerHotbarItemBuilder.playerHiderItemBuilder
            player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.player_hider.slot"), hiderItem)
        }

        if (AdvancedLobby.cfg.getBoolean("hotbar_items.cosmetics.enabled")) {
            val cosmeticsItem = PlayerHotbarItemBuilder.cosmeticsItemBuilder
            player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.cosmetics.slot"), cosmeticsItem)
        }

        if (AdvancedLobby.cfg.getBoolean("hotbar_items.gadget.enabled")) {
            val noGadgetItem = PlayerHotbarItemBuilder.noGadgetItemBuilder
            player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.gadget.slot"), noGadgetItem)
        }

        if (AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.enabled")
            && player.hasPermission("advancedlobby.silentlobby")
            && AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.inHotbar")) {
            val silentLobbyItem = PlayerHotbarItemBuilder.silentLobbyItemBuilder
            player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.slot"), silentLobbyItem)
        }

        if (AdvancedLobby.cfg.getBoolean("hotbar_items.custom_item.enabled")
            && player.hasPermission("advancedlobby.custom_item")) {
            val customItem = PlayerHotbarItemBuilder.customItemBuilder
            player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.custom_item.slot"), customItem)
        }
    }

    private fun hideHiddenPlayers(player: Player){
        for (players in AdvancedLobby.playerHider.keys) {
            if (AdvancedLobby.playerHider[players] == HiderType.VIP) {
                if (!player.hasPermission("advancedlobby.player_hider.bypass")) {
                    players.hidePlayer(AdvancedLobby.getInstance(), player)
                }
            }
            if (AdvancedLobby.playerHider[players] == HiderType.NONE) {
                players.hidePlayer(AdvancedLobby.getInstance(), player)
            }
        }
    }

    private fun hideSilentLobbyPlayers(player: Player){
        for (players in AdvancedLobby.silentLobby) {
            players.hidePlayer(AdvancedLobby.getInstance(), player)
            player.hidePlayer(AdvancedLobby.getInstance(), players)
        }
    }

    private fun fistTimeJoin(player: Player){
        val location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"))
        if (location != null) {
            player.teleport(location)
        }
    }

    private fun joinAtSpawn(player: Player){
        val location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"))
        if (location != null) {
            player.teleport(location)
        }
    }

    private fun displayAdminMessage(player: Player){

        if (AdvancedLobby.updateAvailable) {
            val message = Component.text()
                .append(Component.text("┃ ", NamedTextColor.DARK_GRAY))
                .append(Component.text("AdvancedLobby ", NamedTextColor.AQUA))
                .append(Component.text("┃ ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Download now ", NamedTextColor.GRAY))
                .append(Component.text("▶", NamedTextColor.DARK_GRAY))
                .build()

            val extra = Component.text()
                .append(Component.text("*", NamedTextColor.DARK_GRAY))
                .append(Component.text("click", NamedTextColor.GREEN))
                .append(Component.text("*", NamedTextColor.DARK_GRAY))
                .hoverEvent(
                    HoverEvent.showText(
                        Component.text("» ", NamedTextColor.DARK_GRAY)
                            .append(Component.text("Redirect to ", NamedTextColor.GRAY))
                            .append(Component.text("https://spigotmc.org/", NamedTextColor.AQUA))
                    )
                )
                .clickEvent(
                    ClickEvent.openUrl("https://spigotmc.org/resources/35799/")
                )
                .build()

            val finalMessage = message.append(extra)


            player.sendMessage("")
            player.sendMessage("§8┃ §bAdvancedLobby §8┃ §7A §anew update §7for §bAdvancedLobby §7was found§8.")
            player.sendMessage(finalMessage)
            player.sendMessage("")
        }

        if (AdvancedLobby.errors.isNotEmpty()) {
            player.sendMessage("")
            player.sendMessage("§8┃ §4AdvancedLobby §8┃ §c" + AdvancedLobby.errors.size + " errors §7were found.")
            player.sendMessage("§8┃ §4AdvancedLobby §8┃ §7You can see them by typing §8/§fal errors")
            player.sendMessage("")
        }


    }

    
}