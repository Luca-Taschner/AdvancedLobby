package gg.ninjagaming.advancedlobby.itembuilders

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.ItemBuilder

object PlayerHotbarItemBuilder{
    val teleporterItemBuilder: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.teleporter.material"), 1)
        .setDisplayName(AdvancedLobby.cfg.getString("hotbar_items.teleporter.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.teleporter.lore"))

    val playerHiderItemBuilder: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all.material"), 1)
        .setDisplayName(AdvancedLobby.cfg.getString("hotbar_items.player_hider.show_all.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_all.lore"))

    val cosmeticsItemBuilder: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.cosmetics.material"), 1)
        .setDisplayName(AdvancedLobby.cfg.getString("hotbar_items.cosmetics.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.cosmetics.lore"))

    val noGadgetItemBuilder: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.gadget.unequipped.material"), 1)
        .setDisplayName(AdvancedLobby.cfg.getString("hotbar_items.gadget.unequipped.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.gadget.unequipped.lore"));

    val silentLobbyItemBuilder: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.silentlobby.deactivated.material"), 1)
        .setDisplayName(AdvancedLobby.cfg.getString("hotbar_items.silentlobby.deactivated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.silentlobby.deactivated.lore"))

    val customItemBuilder: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.custom_item.material"), 1)
        .setDisplayName(AdvancedLobby.cfg.getString("hotbar_items.custom_item.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.custom_item.lore"));
}