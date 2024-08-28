package gg.ninjagaming.advancedlobby.itembuilders

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.ItemBuilder

object ShieldItemBuilder {
    val itemStackShieldActivate = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.shield.activated.material"), 1)
        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.activated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.activated.lore"))

    val itemStackShieldDeactivate = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.shield.deactivated.material"), 1)
        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.deactivated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.deactivated.lore"))

    val itemStackShieldMenuItem = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.shield.menu_item.material"), 1)
        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.menu_item.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.menu_item.lore"))
}