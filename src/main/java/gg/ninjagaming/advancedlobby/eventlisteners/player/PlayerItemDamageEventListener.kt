package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent

class PlayerItemDamageEventListener : Listener {
    @EventHandler
    fun onPlayerItemDamage(e: PlayerItemDamageEvent) {
        if (e.item.type != Material.FISHING_ROD)
            return

        if (!e.item.hasItemMeta())
            return

        val itemDisplayName = e.item.itemMeta.displayName() ?: return
        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)

        if (legacyItemName !=
                AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace("%gadget%",
                    AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname")))
            return

        e.isCancelled = true
    }
}