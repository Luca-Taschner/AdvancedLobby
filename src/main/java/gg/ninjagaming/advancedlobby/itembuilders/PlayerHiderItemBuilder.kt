package gg.ninjagaming.advancedlobby.itembuilders

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.ItemBuilder

object PlayerHiderItemBuilder {
    val itemStackShowAll = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all.material"), 1)
        .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_all.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_all.lore"))

    val itemStackShowVIP = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.player_hider.show_vip.material"), 1)
        .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_vip.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_vip.lore"))

    val itemStackShowNone = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.player_hider.show_none.material"), 1)
        .setDisplayName(AdvancedLobby.getString("hotbar_items.player_hider.show_none.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_none.lore"))
}