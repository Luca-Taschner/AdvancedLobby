package gg.ninjagaming.advancedlobby.inventorybuilder

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object CosmeticsHatsInventory {
    private var inventoryInstance: Inventory = buildInventory()

    fun openInventory(player: Player) {
        player.openInventory(inventoryInstance)
    }

    fun updateInventory() {
        inventoryInstance = buildInventory()
    }

    private fun buildInventory():Inventory
    {
        val inventory = Bukkit.createInventory(null, 45, Component.text(AdvancedLobby.getString("inventories.cosmetics_hats.title")))

        val melonBlockItemStack = ItemBuilder(Material.MELON)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.melon_hat.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.melon_hat.lore"))

        val tntItemStack = ItemBuilder(Material.TNT)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.tnt_hat.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.tnt_hat.lore"))

        val glassItemStack = ItemBuilder(Material.GLASS)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.glass_hat.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.glass_hat.lore"))

        val spongeItemStack = ItemBuilder(Material.SPONGE)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.sponge_hat.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.sponge_hat.lore"))

        val pumpkinItemStack = ItemBuilder(Material.PUMPKIN)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.pumpkin_hat.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.pumpkin_hat.lore"))

        val cactusItemStack = ItemBuilder(Material.CACTUS)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.cactus_hat.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.cactus_hat.lore"))

        val deleteItemItemStack = ItemBuilder(Material.RED_DYE)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.disable.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.disable.lore"))

        val goBackItemStack = ItemBuilder(Material.BARRIER)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.go_back.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_hats.go_back.lore"))

        inventory.setItem(11, melonBlockItemStack)
        inventory.setItem(12, tntItemStack)
        inventory.setItem(13, glassItemStack)
        inventory.setItem(14, spongeItemStack)
        inventory.setItem(15, pumpkinItemStack)
        inventory.setItem(16, cactusItemStack)

        for(i in 36..44)
        {
            inventory.setItem(i, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r").build())
        }

        inventory.setItem(40, deleteItemItemStack)
        inventory.setItem(44, goBackItemStack)

        return inventory
    }
}