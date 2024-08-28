package gg.ninjagaming.advancedlobby.misc

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.extras.cosmetics.Cosmetics
import gg.ninjagaming.advancedlobby.itembuilders.PlayerHiderItemBuilder
import gg.ninjagaming.advancedlobby.misc.VParticle.getParticleEnum
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object SilentLobby {
    val itemStackSilentLobbyActivate: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.silentlobby.activated.material"), 1)
        .setDisplayName(AdvancedLobby.getString("hotbar_items.silentlobby.activated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.silentlobby.activated.lore"))

    val itemStackSilentLobbyDeactivate: ItemBuilder = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.silentlobby.deactivated.material"), 1)
        .setDisplayName(AdvancedLobby.getString("hotbar_items.silentlobby.deactivated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.silentlobby.deactivated.lore"))

    fun addPlayer(player: Player){
        AdvancedLobby.silentLobby.add(player)
        AdvancedLobby.playerHider[player] = HiderType.NONE

        player.sendMessage(Locale.SILENTLOBBY_JOIN.getMessage(player))
        AdvancedLobby.playSound(player, player.location, "silentlobby.enable_silentlobby")

        player.spawnParticle(getParticleEnum("EXPLOSION_HUGE"), player.location, 1)
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20, 0))

        val players = Bukkit.getOnlinePlayers()
        players.forEach{
            if (it == player)
                return

            it.hidePlayer(AdvancedLobby.instance!!, player)
            player.hidePlayer(AdvancedLobby.instance!!, it)
        }
        if (AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.inHotbar"))
            player.inventory.setItemInMainHand(itemStackSilentLobbyActivate)

        player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.player_hider.slot"), PlayerHiderItemBuilder.itemStackShowNone)

        if (Cosmetics.balloons.containsKey(player)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                AdvancedLobby.instance!!,
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

            it.showPlayer(AdvancedLobby.instance!!, player)
            player.showPlayer(AdvancedLobby.instance!!, it)

            if (!AdvancedLobby.silentLobby.contains(it))
                return

            if (!AdvancedLobby.playerHider.containsKey(it)){
                it.showPlayer(AdvancedLobby.instance!!, player)
                player.showPlayer(AdvancedLobby.instance!!, it)
                return
            }

            if (AdvancedLobby.playerHider[it] == HiderType.VIP && player.hasPermission("advancedlobby.player_hider.bypass")){
                it.showPlayer(AdvancedLobby.instance!!, player)
            }

            if (AdvancedLobby.playerHider[it] == HiderType.NONE){
                it.showPlayer(AdvancedLobby.instance!!, player)
            }

            player.showPlayer(AdvancedLobby.instance!!, it)
        }
    }
}