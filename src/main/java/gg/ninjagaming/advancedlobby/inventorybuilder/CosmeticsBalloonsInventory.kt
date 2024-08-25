package gg.ninjagaming.advancedlobby.inventorybuilder

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object CosmeticsBalloonsInventory {
    private var inventoryInstance: Inventory = buildInventory()

    fun openInventory(player: Player) {
        player.openInventory(inventoryInstance)
    }

    fun updateInventory() {
        inventoryInstance = buildInventory()
    }


    private fun buildInventory(): Inventory {
        val inventory = Bukkit.createInventory(null, 45, Component.text(AdvancedLobby.getString("inventories.cosmetics_balloons.title")))

        val yellowBalloonItemStack = ItemBuilder(Material.YELLOW_TERRACOTTA)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.yellow_balloon.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.yellow_balloon.lore"))

        val redBalloonItemStack = ItemBuilder(Material.RED_TERRACOTTA)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.red_balloon.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.red_balloon.lore"))

        val greenBalloonItemStack = ItemBuilder(Material.LIME_TERRACOTTA)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.green_balloon.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.green_balloon.lore"))

        val blueBalloonItemStack = ItemBuilder(Material.LIGHT_BLUE_TERRACOTTA)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.blue_balloon.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.blue_balloon.lore"))

        val hayBalloonItemStack = ItemBuilder(Material.HAY_BLOCK)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.hay_block_balloon.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.hay_block_balloon.lore"))

        val seaLanternItemStack = ItemBuilder(Material.SEA_LANTERN)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.sea_lantern_balloon.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.sea_lantern_balloon.lore"))

        val bookshelfItemStack = ItemBuilder(Material.BOOKSHELF)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.bookshelf_balloon.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.bookshelf_balloon.lore"))

        val noteBlockItemStack = ItemBuilder(Material.NOTE_BLOCK)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.note_block_balloon.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.note_block_balloon.lore"))

        val deleteItemItemStack = ItemBuilder(Material.RED_DYE)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.disable.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.disable.lore"))

        val goBackItemStack = ItemBuilder(Material.BARRIER)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_balloons.go_back.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_balloons.go_back.lore"))

        inventory.setItem(11, yellowBalloonItemStack)
        inventory.setItem(12, redBalloonItemStack)
        inventory.setItem(13, greenBalloonItemStack)
        inventory.setItem(14, blueBalloonItemStack)
        inventory.setItem(15, hayBalloonItemStack)
        inventory.setItem(20, seaLanternItemStack)
        inventory.setItem(21, bookshelfItemStack)
        inventory.setItem(22, noteBlockItemStack)

        for(i in 36..44)
        {
            inventory.setItem(i, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r").build())
        }

        inventory.setItem(40, deleteItemItemStack)
        inventory.setItem(44, goBackItemStack)

        return inventory
    }
}