package gg.ninjagaming.advancedlobby.inventorybuilder

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object CosmeticsInventory {
    private var inventoryInstance: Inventory = buildInventory()

    fun openInventory(player: Player) {
        player.openInventory(inventoryInstance)
    }

    fun updateInventory() {
        inventoryInstance = buildInventory()
    }


    private fun buildInventory(): Inventory {
        val inventory = Bukkit.createInventory(null, 45, Component.text(AdvancedLobby.getString("inventories.cosmetics.title")))

        val hatsItemStack = ItemBuilder(Material.PUMPKIN)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.hats.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.hats.lore"))
        val particlesItemStack = ItemBuilder(Material.BLAZE_POWDER)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.particles.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.particles.lore"))
        val gadgetsItemStack = ItemBuilder(Material.FISHING_ROD)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.gadgets.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.gadgets.lore"))
        val balloonsItemStack = ItemBuilder(Material.LEAD)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics.balloons.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics.balloons.lore"))

        inventory.setItem(19, hatsItemStack)
        inventory.setItem(21, particlesItemStack)
        inventory.setItem(23, balloonsItemStack)
        inventory.setItem(25, gadgetsItemStack)

        for (i in 0..8) {
            inventory.setItem(i, ItemBuilder(ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)).setDisplayName("§r").build())
        }

        for (i in 36..44) {
            inventory.setItem(i, ItemBuilder(ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)).setDisplayName("§r").build())
        }
        return inventory
    }
}