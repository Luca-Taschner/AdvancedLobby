package gg.ninjagaming.advancedlobby.misc

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.cosmetics.Cosmetics
import de.cyne.advancedlobby.crossversion.VParticle
import de.cyne.advancedlobby.itembuilder.ItemBuilder
import de.cyne.advancedlobby.locale.Locale
import de.cyne.advancedlobby.misc.HiderType
import gg.ninjagaming.advancedlobby.itembuilders.PlayerHiderItemBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object SilentLobby {
    val itemStackSilentLobbyActivate: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.silentlobby.activated.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.activated.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.silentlobby.activated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.silentlobby.activated.lore"))

    val itemStackSilentLobbyDeactivate: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.silentlobby.deactivated.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.deactivated.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.silentlobby.deactivated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.silentlobby.deactivated.lore"))

    fun addPlayer(player: Player){
        AdvancedLobby.silentLobby.add(player)
        AdvancedLobby.playerHider[player] = HiderType.NONE

        player.sendMessage(Locale.SILENTLOBBY_JOIN.getMessage(player))
        AdvancedLobby.playSound(player, player.location, "silentlobby.enable_silentlobby")

        VParticle.spawnParticle(player, "EXPLOSION_HUGE", player.location, 1)
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20, 0))

        val players = Bukkit.getOnlinePlayers()
        players.forEach{
            if (it == player)
                return

            it.hidePlayer(AdvancedLobby.getInstance(), player)
            player.hidePlayer(AdvancedLobby.getInstance(), it)
        }
        if (AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.inHotbar"))
            player.inventory.setItemInMainHand(itemStackSilentLobbyActivate)

        player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.player_hider.slot"), PlayerHiderItemBuilder.itemStackShowNone)

        if (Cosmetics.balloons.containsKey(player)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.getInstance(),
                { Cosmetics.balloons[player]!!.remove() }, 5L
            )
        }
    }

    fun removePlayer(player: Player){
        AdvancedLobby.silentLobby.remove(player)
        AdvancedLobby.playerHider.remove(player)

        player.sendMessage(Locale.SILENTLOBBY_LEAVE.getMessage(player))
        AdvancedLobby.playSound(player, player.location, "silentlobby.disable_silentlobby")
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20, 0))

        if (AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.inHotbar"))
            player.inventory.setItemInMainHand(itemStackSilentLobbyDeactivate)

        player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.player_hider.slot"), PlayerHiderItemBuilder.itemStackShowAll)

        val players = Bukkit.getOnlinePlayers()
        players.forEach{
            if (it == player)
                return

            it.showPlayer(AdvancedLobby.getInstance(), player)
            player.showPlayer(AdvancedLobby.getInstance(), it)

            if (!AdvancedLobby.silentLobby.contains(it))
                return

            if (!AdvancedLobby.playerHider.containsKey(it)){
                it.showPlayer(AdvancedLobby.getInstance(), player)
                player.showPlayer(AdvancedLobby.getInstance(), it)
                return
            }

            if (AdvancedLobby.playerHider[it] == HiderType.VIP && player.hasPermission("advancedlobby.player_hider.bypass")){
                it.showPlayer(AdvancedLobby.getInstance(), player)
            }

            if (AdvancedLobby.playerHider[it] == HiderType.NONE){
                it.showPlayer(AdvancedLobby.getInstance(), player)
            }

            player.showPlayer(AdvancedLobby.getInstance(), it)
        }
    }
}