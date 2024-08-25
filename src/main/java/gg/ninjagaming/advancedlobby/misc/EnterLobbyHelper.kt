package gg.ninjagaming.advancedlobby.misc

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.misc.LocationManager
import gg.ninjagaming.advancedlobby.itembuilders.PlayerHotbarItemBuilder
import gg.ninjagaming.advancedlobby.titleapi.TitleApi.sendTitle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.entity.Player

object EnterLobbyHelper {
    /**
     * Sets the hotbar items for the specified player based on the configuration settings.
     *
     * @param player the player for whom to set the hotbar items
     */
    fun setPlayerHotbarItems(player: Player){
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

    /**
     * Hides hidden players for the given player.
     *
     * @param player the player for whom to hide hidden players
     */
    fun hideHiddenPlayers(player: Player) {
        for (players in AdvancedLobby.playerHider.keys) {
            if (AdvancedLobby.playerHider[players] == HiderType.VIP) {
                if (!player.hasPermission("advancedlobby.player_hider.bypass")) {
                    players.hidePlayer(AdvancedLobby.getInstance(),player)
                }
            }
            if (AdvancedLobby.playerHider[players] == HiderType.NONE) {
                players.hidePlayer(AdvancedLobby.getInstance(),player)
            }
        }
    }

    /**
     * Hides the silent lobby players for the specified player.
     *
     * @param player the player for whom to hide silent lobby players
     */
    fun hideSilentLobbyPlayers(player: Player){
        for (players in AdvancedLobby.silentLobby) {
            players.hidePlayer(AdvancedLobby.getInstance(), player)
            player.hidePlayer(AdvancedLobby.getInstance(), players)
        }
    }

    /**
     * Sets the gamemode of the specified player based on the value from the configuration file.
     *
     * @param player the player whose gamemode will be set
     */
    fun setPlayerGamemode(player: Player){
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
     * Sets the values of a player based on the configuration settings.
     *
     * @param player the player whose values will be set
     */
    fun setPlayerValues(player: Player){
        player.health = AdvancedLobby.cfg.getDouble("player_join.health")
        player.foodLevel = 20

        player.allowFlight = false
        player.isFlying = false

        player.fireTicks = 0

        player.level = 0
        player.exp = 0f
    }

    /**
     * Teleports the player to the spawn location specified in the configuration.
     *
     * @param player the player to teleport
     */
    fun joinAtSpawn(player: Player){
        val location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"))
        if (location != null) {
            player.teleport(location)
        }
    }

    /**
     * Clears the inventory of the specified player.
     *
     * @param player the player whose inventory will be cleared
     */
    fun clearPlayerInventory(player: Player){
        val inventory = player.inventory
        inventory.clear()
        inventory.setArmorContents(null)
    }

    /**
     * Sends a title to the specified player.
     *
     * @param player the player to send the title to
     */
    fun sendPlayerTitle(player: Player){
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
     * Displays an admin message to the specified player.
     *
     * If an update is available for the AdvancedLobby plugin, a message is displayed with a download link.
     *
     * If there are any errors recorded in the AdvancedLobby plugin, a message is displayed with the number of errors found.
     *
     * @param player the player to display the message to
     */
    fun displayAdminMessage(player: Player){

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