package gg.ninjagaming.advancedlobby.inventorybuilder

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.itembuilder.ItemBuilder
import gg.ninjagaming.advancedlobby.misc.SilentLobby
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag


object CompassInventory{
    private var compassInventory: Inventory = buildInventory()

    fun openInventory(player: Player)
    {
        val inventory = compassInventory

        if (AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.enabled") && !AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.inHotbar")) {
            if (AdvancedLobby.silentLobby.contains(player))
            {
                inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.slot"), SilentLobby.itemStackSilentLobbyActivate)
            } else {
                inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.slot"), SilentLobby.itemStackSilentLobbyDeactivate)
            }
        }


        player.openInventory(inventory)
    }

    fun updateInventory()
    {
        compassInventory = buildInventory()
    }


    private fun buildInventory(): Inventory {
        val inventory = Bukkit.createInventory(null,AdvancedLobby.getInt("inventories.teleporter.rows")*9, Component.text(AdvancedLobby.getString("inventories.teleporter.title")))

        val items = AdvancedLobby.cfg.getConfigurationSection("inventories.teleporter.items")?.getKeys(false)?: return inventory


        items.forEach {
            val material = Material.getMaterial(
                AdvancedLobby.cfg.getString("inventories.teleporter.items.$it.material") ?: return@forEach
            )

            val displayName = AdvancedLobby.cfg.getString("inventories.teleporter.items.$it.displayname") ?: return@forEach

            val subId = AdvancedLobby.cfg.getInt("inventories.teleporter.items.$it.subid")
            val slot = AdvancedLobby.cfg.getInt("inventories.teleporter.items.$it.slot")
            val lore = AdvancedLobby.cfg.getStringList("inventories.teleporter.items.$it.lore")

            val itemStack = ItemBuilder(material, 1, subId.toShort()).setDisplayName(displayName).setLobbyItemLore(lore).addLobbyItemFlags(
                ItemFlag.HIDE_ATTRIBUTES)
            inventory.setItem(slot, itemStack)


        }




        return inventory
    }
}