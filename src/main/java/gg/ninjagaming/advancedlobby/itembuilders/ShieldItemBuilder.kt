package gg.ninjagaming.advancedlobby.itembuilders

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.itembuilder.ItemBuilder

object ShieldItemBuilder {
    val itemStackShieldActivate = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.shield.activated.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.shield.activated.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.activated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.activated.lore"))!!

    val itemStackShieldDeactivate = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.shield.deactivated.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.shield.deactivated.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.deactivated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.deactivated.lore"))!!

    val itemStackShieldMenuItem = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.shield.menu_item.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.shield.menu_item.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.menu_item.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.menu_item.lore"))!!
}