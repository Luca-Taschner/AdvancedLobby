package gg.ninjagaming.advancedlobby.itembuilders

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.itembuilder.ItemBuilder

object PlayerHiderItemBuilder {
    val itemStackShowAll = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_all.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_all.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_all.lore"))

    val itemStackShowVIP = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.player_hider.show_vip.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_vip.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_vip.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_vip.lore"))

    val itemStackShowNone = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.player_hider.show_none.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_none.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_none.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_none.lore"))
}