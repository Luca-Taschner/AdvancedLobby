package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.inventorybuilder.CompassInventory
import gg.ninjagaming.advancedlobby.inventorybuilder.CosmeticsInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class PlayerInteractListener: Listener {

    private val blockedMaterials = arrayOf(
        Material.CHEST,
        Material.ENDER_CHEST,
        Material.TRAPPED_CHEST,
        Material.CRAFTING_TABLE,
        Material.FURNACE,
        Material.ENCHANTING_TABLE,
        Material.ANVIL,
        Material.BLUE_BED,
        Material.BROWN_BED,
        Material.CYAN_BED,
        Material.GRAY_BED,
        Material.GREEN_BED,
        Material.LIGHT_BLUE_BED,
        Material.LIGHT_GRAY_BED,
        Material.LIME_BED,
        Material.MAGENTA_BED,
        Material.ORANGE_BED,
        Material.PINK_BED,
        Material.PURPLE_BED,
        Material.RED_BED,
        Material.WHITE_BED,
        Material.YELLOW_BED,
        Material.JUKEBOX,
        Material.BEACON,
        Material.DISPENSER,
        Material.LEVER,
        Material.STONE_BUTTON,
        Material.ACACIA_BUTTON,
        Material.BIRCH_BUTTON,
        Material.DARK_OAK_BUTTON,
        Material.JUNGLE_BUTTON,
        Material.OAK_BUTTON,
        Material.SPRUCE_BUTTON,
        Material.DAYLIGHT_DETECTOR,
        Material.HOPPER,
        Material.DROPPER,
        Material.BREWING_STAND,
        Material.COMPARATOR,
        Material.REPEATER,
        Material.DRAGON_EGG,
        Material.NOTE_BLOCK,
        Material.CAKE,
        Material.FLOWER_POT)

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if(!AdvancedLobby.multiWorld_mode || !AdvancedLobby.lobbyWorlds.contains(player.world))
            return

        if (event.action == Action.PHYSICAL)
            physicalAction(player, event)

        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK)
            clickBlockAction(player, event)


        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)
            rightClickAction(player, event)
    }

    private fun physicalAction(player: Player, event: PlayerInteractEvent) {
        if (!AdvancedLobby.build.contains(player))
            event.isCancelled = true
    }

    private fun clickBlockAction(player: Player, event: PlayerInteractEvent) {
        if (!blockedMaterials.contains(event.clickedBlock?.type))
            return

        if(AdvancedLobby.cfg.getBoolean("disable_player_interaction") && !AdvancedLobby.build.contains(player))
            event.isCancelled = true


    }

    private fun rightClickAction(player: Player, event: PlayerInteractEvent) {
        val itemStack = player.inventory.itemInMainHand

        when(itemStack.type){
            AdvancedLobby.getMaterial("hotbar_items.teleporter.material") -> compassAction(player, event, itemStack)
            AdvancedLobby.getMaterial("hotbar_items.cosmetics.material") -> cosmeticsAction(player, event, itemStack)
            Material.FEATHER -> gadgetsAction(player, event, itemStack)
            AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all_material") -> hiderAction(player, event)
            AdvancedLobby.getMaterial("hotbar_items.player_hider.show_vip.material") -> hiderAction(player, event)
            AdvancedLobby.getMaterial("hotbar_items.player_hider.show_none.material") -> hiderAction(player, event)
            AdvancedLobby.getMaterial("hotbar_items.silent_lobby.activated.material") -> silentLobbyAction(player, event)
            AdvancedLobby.getMaterial("hotbar_items.silent_lobby.deactivated.material") -> silentLobbyAction(player, event)
            AdvancedLobby.getMaterial("hotbar_items.shield.activated.material") -> shieldAction(player, event)
            AdvancedLobby.getMaterial("hotbar_items.shield.deactivated.material") -> shieldAction(player, event)
            AdvancedLobby.getMaterial("hotbar_items.custom_item.material") -> customItemAction(player, event)
            else -> return

        }

    }

    private fun compassAction(player: Player, event: PlayerInteractEvent, item: ItemStack) {
        if (!item.hasItemMeta() || item.itemMeta.displayName()?.equals(AdvancedLobby.getString("hotbar_items.teleporter.displayname")) == false)
            return

        event.isCancelled = true

        AdvancedLobby.playSound(player, player.location,"teleporter.open_inventory")
        CompassInventory.openInventory(player)
    }

    private fun cosmeticsAction(player: Player, event: PlayerInteractEvent, item:ItemStack) {
        if (!item.hasItemMeta() || item.itemMeta.displayName()?.equals(AdvancedLobby.getString("hotbar_items.cosmetics.displayname")) == false)
            return

        event.isCancelled = true
        AdvancedLobby.playSound(player, player.location, "cosmetics.open_inventory")
        CosmeticsInventory.openInventory(player)
    }

    private fun gadgetsAction(player: Player, event: PlayerInteractEvent, item: ItemStack) {

    }

    private fun hiderAction(player: Player, event: PlayerInteractEvent) {

    }

    private fun silentLobbyAction(player: Player, event: PlayerInteractEvent) {

    }

    private fun shieldAction(player: Player, event: PlayerInteractEvent) {

    }

    private fun customItemAction(player: Player, event: PlayerInteractEvent) {

    }
}