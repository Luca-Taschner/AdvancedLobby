package gg.ninjagaming.advancedlobby.inventorybuilder

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.itembuilder.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag

object CosmeticsParticlesInventory {
    private var inventoryInstance: Inventory = buildInventory()

    fun openInventory(player: Player) {
        player.openInventory(inventoryInstance)
    }

    fun updateInventory() {
        inventoryInstance = buildInventory()
    }


    private fun buildInventory(): Inventory {
        val inventory = Bukkit.createInventory(null, 45, Component.text(AdvancedLobby.getString("inventories.cosmetics_particles.title")))

        val redstoneItemStack = ItemBuilder(Material.REDSTONE)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.heart_particles.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.heart_particles.lore"))

        val recordPlateItemStack = ItemBuilder(Material.MUSIC_DISC_STRAD)
            .addLobbyItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.music_particles.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.music_particles.lore"))

        val fireballItemStack = ItemBuilder(Material.FIRE_CHARGE)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.flames_particles.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.flames_particles.lore"))

        val emeraldItemStack = ItemBuilder(Material.EMERALD)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.villager_particles.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.villager_particles.lore"))

        val netherStarItemStack = ItemBuilder(Material.NETHER_STAR)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.rainbow_particles.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.rainbow_particles.lore"))

        val deleteItemItemStack = ItemBuilder(Material.RED_DYE)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.disable.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.disable.lore"))

        val goBackItemStack = ItemBuilder(Material.BARRIER)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_particles.go_back.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_particles.go_back.lore"))

        inventory.setItem(11, redstoneItemStack)
        inventory.setItem(12, recordPlateItemStack)
        inventory.setItem(13, fireballItemStack)
        inventory.setItem(14, emeraldItemStack)
        inventory.setItem(15, netherStarItemStack)

        for (i in 36..44) {
            inventory.setItem(i, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r").build())
        }

        inventory.setItem(40, deleteItemItemStack)
        inventory.setItem(44, goBackItemStack)

        return inventory
    }
}