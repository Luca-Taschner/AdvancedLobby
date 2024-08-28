package gg.ninjagaming.advancedlobby.misc.extras.gadgets

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.itembuilders.ShieldItemBuilder
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object ShieldGadget {
    fun shieldActivate(player: Player){
        AdvancedLobby.shield.add(player)
        AdvancedLobby.playSound(player, player.location, "shield.enable_shield")
        player.sendMessage(Locale.SHIELD_ACTIVATE.getMessage(player))
        player.inventory.setItemInMainHand(ShieldItemBuilder.itemStackShieldActivate)

        val entities = player.getNearbyEntities(2.5, 2.5, 2.5)
        entities.forEach{ itEntity ->
            if (itEntity !is Player)
                return

            if (itEntity.hasMetadata("NPC") || AdvancedLobby.silentLobby.contains(player) || AdvancedLobby.silentLobby.contains(itEntity))
                return

            if (itEntity.hasPermission("advancedlobby.shield.bypass"))
                return

            val nearbyPlayerVector: Vector = itEntity.location.toVector()
            val playerVector: Vector = player.location.toVector()
            val velocity = nearbyPlayerVector.clone().subtract(playerVector).normalize().multiply(0.5).setY(0.25)
            itEntity.velocity = velocity

            player.playEffect(player.location, Effect.ENDER_SIGNAL, "")

            val players = Bukkit.getOnlinePlayers()
            players.forEach{ itPlayer ->
                if (itPlayer == player)
                    return

                if (AdvancedLobby.silentLobby.contains(player) || AdvancedLobby.silentLobby.contains(itPlayer))
                    return

                if (itEntity.hasPermission("advancedlobby.shield.bypass"))
                    return

                itPlayer.playEffect(player.location, Effect.ENDER_SIGNAL, "")
            }
        }
    }

    fun shieldDeactivate(player: Player){
        AdvancedLobby.shield.remove(player)
        AdvancedLobby.playSound(player, player.location, "shield.disable_shield")
        player.sendMessage(Locale.SHIELD_DEACTIVATE.getMessage(player))
        player.inventory.setItemInMainHand(ShieldItemBuilder.itemStackShieldDeactivate)
    }
}