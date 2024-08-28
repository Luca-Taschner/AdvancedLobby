package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishEventListener : Listener {
    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        val player = event.player
        var hook: Entity? = null

        try {
            val hookEntity = PlayerFishEvent::class.java.getDeclaredField("hookEntity")
            hookEntity.isAccessible = true
            hook = hookEntity[event] as Entity
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val gadgetName = AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace("%gadget%".toRegex(), AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname"))

        if (!player.inventory.itemInMainHand.hasItemMeta())
            return

        val itemDisplayName= player.inventory.itemInMainHand.itemMeta.displayName() ?: return
        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)

        if (legacyItemName != gadgetName)
            return

        if (!(!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(player.world)))
            return

        if (AdvancedLobby.build.contains(player))
            return

        if (hook!!.location.subtract(0.0, 1.0, 0.0).block.type == Material.AIR)
            return

        val pV = player.location.toVector()
        val hV = hook.location.toVector()
        val v = hV.clone().subtract(pV).normalize().multiply(1.5).setY(0.5)

        player.velocity = v
        AdvancedLobby.playSound(player, player.location, "gadgets.grappling_hook")

        for (otherPlayer in Bukkit.getOnlinePlayers()) {
            if (player !== otherPlayer) {
                if (!AdvancedLobby.silentLobby.contains(player) && !AdvancedLobby.silentLobby.contains(
                        otherPlayer
                    ) && !AdvancedLobby.playerHider.containsKey(otherPlayer)
                ) {
                    AdvancedLobby.playSound(otherPlayer, player.location, "gadgets.grappling_hook")
                }
            }
        }
    }
}
