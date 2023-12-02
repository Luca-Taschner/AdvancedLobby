package gg.ninjagaming.advancedlobby.inventorybuilder

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.AdvancedLobby.cfg
import de.cyne.advancedlobby.itembuilder.ItemBuilder
import gg.ninjagaming.advancedlobby.itembuilders.ShieldItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object CosmeticsGadgetsInventory {
    private var compassInventory: Inventory = buildInventory()

    fun openInventory(player: Player)
    {
        if (cfg.getBoolean("hotbar_items.shield.enabled"))
            compassInventory.setItem(15,ShieldItemBuilder.itemStackShieldMenuItem)

        player.openInventory(compassInventory)
    }

    fun updateInventory()
    {
        compassInventory = buildInventory()
    }


    private fun buildInventory(): Inventory {
        val inventory = Bukkit.createInventory(null, 45, Component.text(AdvancedLobby.getString("inventories.cosmetics_gadgets.title")))

        val fishingRodItemStack = ItemBuilder(Material.FISHING_ROD)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_gadgets.grappling_hook_gadget.lore"))

        val feather = ItemBuilder(Material.FEATHER)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_gadgets.rocket_jump_gadget.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_gadgets.rocket_jump_gadget.lore"))

        val deleteItemItemStack = ItemBuilder(Material.RED_DYE)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_gadgets.disable.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_gadgets.disable.lore"))

        val goBackItemStack = ItemBuilder(Material.BARRIER)
            .setDisplayName(AdvancedLobby.getString("inventories.cosmetics_gadgets.go_back.displayname"))
            .setLobbyItemLore(AdvancedLobby.cfg.getStringList("inventories.cosmetics_gadgets.go_back.lore"))

        inventory.setItem(11, fishingRodItemStack)
        inventory.setItem(13, feather)

        for (i in 36..44){
            inventory.setItem(i, ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r").build())
        }

        inventory.setItem(40,deleteItemItemStack)
        inventory.setItem(44,goBackItemStack)




        return inventory
    }
}