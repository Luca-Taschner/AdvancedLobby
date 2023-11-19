package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.cosmetics.Cosmetics
import de.cyne.advancedlobby.crossversion.VMaterial
import de.cyne.advancedlobby.crossversion.VParticle
import de.cyne.advancedlobby.itembuilder.ItemBuilder
import de.cyne.advancedlobby.locale.Locale
import de.cyne.advancedlobby.misc.LocationManager
import gg.ninjagaming.advancedlobby.inventorybuilder.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryClickEventListener: Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent){
        val player: Player = event.whoClicked as Player

        if (!AdvancedLobby.multiWorld_mode && !AdvancedLobby.lobbyWorlds.contains(player.world))
            return

        if(AdvancedLobby.build.contains(player))
            return

        if (event.currentItem == null)
            return

        event.isCancelled= true

        when(event.view.originalTitle){
            AdvancedLobby.getString("inventories.teleporter.title") -> compassClick(player,event)
            AdvancedLobby.getString("inventories.cosmetics.title") -> cosmeticsClick(player,event)
            AdvancedLobby.getString("inventories.cosmetics_hats.title") -> cosmeticsHatClick(player,event)
            AdvancedLobby.getString("inventories.cosmetics_particles.title") -> cosmeticsParticlesClick(player,event)
            AdvancedLobby.getString("inventories.cosmetics_balloons.title") -> cosmeticsBalloonsClick(player,event)
            AdvancedLobby.getString("inventories.cosmetics_gadgets.title") -> cosmeticsGadgetsClick(player,event)
            else -> {return}
        }


    }

    private fun compassClick(player: Player, event: InventoryClickEvent){

        val configurationSectionKeys =
            AdvancedLobby.getInstance().config.getConfigurationSection("inventories.teleporter.items")?.getKeys(false)
                ?: return

        configurationSectionKeys.forEach{
            val material = AdvancedLobby.getMaterial("inventories.teleporter.items.$it.material")

            if (event.currentItem?.type != material)
                return@forEach

            val location = LocationManager.getLocation(AdvancedLobby.cfg.getString("inventories.teleporter.items.$it.location"))

            if (location == null){
                player.closeInventory()

                if(player.hasPermission("advancedlobby.admin")) {
                    player.sendMessage(
                        Locale.COMPASS_LOC_NOT_FOUND_ADMIN.getMessage(player).replace(
                            "%location%",
                            AdvancedLobby.cfg.getString("inventories.teleporter.items.$it.location")!!
                        ))
                    return@forEach
                }
                player.sendMessage(
                    Locale.COMPASS_LOC_NOT_FOUND.getMessage(player).replace(
                        "%location%",
                        AdvancedLobby.cfg.getString("inventories.teleporter.items.$it.location")!!
                    )
                )
                return@forEach
            }

            player.teleport(location)
            AdvancedLobby.playSound(player, player.location, "teleporter.teleport")
            VParticle.spawnParticle(player, "SPELL_WITCH", location, 64, 0.0, 0.0, 0.0, 0.1)

            val players = Bukkit.getOnlinePlayers()

            players.forEach {itPlayer: Player ->
                if (itPlayer != player)
                {
                    if(!AdvancedLobby.playerHider.containsKey(itPlayer) && !AdvancedLobby.silentLobby.contains(itPlayer) && !AdvancedLobby.silentLobby.contains(player))
                    {
                        VParticle.spawnParticle(itPlayer, "SPELL_WITCH", location, 64, 0.0, 0.0, 0.0, 0.1)
                    }
                }
            }
        }
    }

    private fun cosmeticsClick(player: Player, event: InventoryClickEvent) {

        when(event.currentItem?.type){
            Material.PUMPKIN -> CosmeticsHatsInventory.openInventory(player)
            Material.BLAZE_POWDER -> CosmeticsParticlesInventory.openInventory(player)
            VMaterial.LEAD.type ->  CosmeticsBalloonsInventory.openInventory(player)
            Material.FISHING_ROD -> CosmeticsGadgetsInventory.openInventory(player)

            else -> {return}
        }
        AdvancedLobby.playSound(player, player.location, "cosmetics.change_page")

    }

    private fun cosmeticsHatClick(player: Player,event: InventoryClickEvent){
        if (!validateCosmeticsClick(player,event)) return

        if(event.currentItem?.type == VMaterial.RED_DYE.type){
            AdvancedLobby.playSound(player, player.location, "cosmetics.disable_cosmetic")
            player.closeInventory()

            if(Cosmetics.hats.containsKey(player)){
                Cosmetics.hats.remove(player)
                player.inventory.helmet = null
                player.sendMessage(Locale.COSMETICS_HATS_DISABLE.getMessage(player))
                return
            }

            player.sendMessage(Locale.COSMETICS_HATS_DISABLE_ERROR.getMessage(player))
            return
        }
        player.closeInventory()

        AdvancedLobby.playSound(player, player.location, "cosmetics.equip_cosmetic")
        Cosmetics.equipHat(player, Cosmetics.HatType.valueOf(event.currentItem?.type.toString()))
        return

    }

    private fun cosmeticsParticlesClick(player: Player, event: InventoryClickEvent){
        if (!validateCosmeticsClick(player,event)) return

        if (event.currentItem?.type == VMaterial.RED_DYE.type) {
            player.closeInventory()
            AdvancedLobby.playSound(player, player.location, "cosmetics.disable_cosmetic")
            if (Cosmetics.particles.containsKey(player)) {
                Cosmetics.particles.remove(player)
                player.sendMessage(Locale.COSMETICS_PARTICLES_DISABLE.getMessage(player))
                return
            }
            player.sendMessage(Locale.COSMETICS_PARTICLES_DISABLE_ERROR.getMessage(player))
            return
        }


        AdvancedLobby.playSound(player, player.location, "cosmetics.equip_cosmetic")

        when(event.currentItem?.type){
            Material.REDSTONE ->{
                if (!player.hasPermission("advancedlobby.cosmetics.particles.heart")){
                    player.sendMessage(
                        Locale.COSMETICS_PARTICLES_NO_PERMISSION.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.heart_particles.displayname")))
                    return
                }

                Cosmetics.particles[player] = Cosmetics.ParticleType.HEART
                player.sendMessage(
                    Locale.COSMETICS_PARTICLES_EQUIP.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.heart_particles.displayname")))
            }

            VMaterial.MUSIC_DISC_STRAD.type->{
                if (!player.hasPermission("advancedlobby.cosmetics.particles.music")){
                    player.sendMessage(
                        Locale.COSMETICS_PARTICLES_NO_PERMISSION.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.music_particles.displayname")))
                    return
                }

                Cosmetics.particles[player] = Cosmetics.ParticleType.MUSIC
                player.sendMessage(
                    Locale.COSMETICS_PARTICLES_EQUIP.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.music_particles.displayname")))
            }

            VMaterial.FIRE_CHARGE.type->{
                if (!player.hasPermission("advancedlobby.cosmetics.particles.flames")){
                    player.sendMessage(
                        Locale.COSMETICS_PARTICLES_NO_PERMISSION.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.flames_particles.displayname")))
                    return
                }

                Cosmetics.particles[player] = Cosmetics.ParticleType.FLAMES
                player.sendMessage(
                    Locale.COSMETICS_PARTICLES_EQUIP.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.flames_particles.displayname")))
            }

            Material.EMERALD ->{
                if (!player.hasPermission("advancedlobby.cosmetics.particles.villager")){
                    player.sendMessage(
                        Locale.COSMETICS_PARTICLES_NO_PERMISSION.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.villager_particles.displayname")))
                    return
                }

                Cosmetics.particles[player] = Cosmetics.ParticleType.VILLAGER
                player.sendMessage(
                    Locale.COSMETICS_PARTICLES_EQUIP.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.villager_particles.displayname")))
            }

            Material.NETHER_STAR ->{
                if (!player.hasPermission("advancedlobby.cosmetics.particles.rainbow")){
                    player.sendMessage(
                        Locale.COSMETICS_PARTICLES_NO_PERMISSION.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.rainbow_particles.displayname")))
                    return
                }

                Cosmetics.particles[player] = Cosmetics.ParticleType.RAINBOW
                player.sendMessage(
                    Locale.COSMETICS_PARTICLES_EQUIP.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.rainbow_particles.displayname")))
            }

            else -> {return}
        }

        player.closeInventory()
    }

    private fun cosmeticsBalloonsClick(player: Player,event: InventoryClickEvent){
        if (!validateCosmeticsClick(player,event)) return

        if (event.currentItem?.type == VMaterial.RED_DYE.type) {
            player.closeInventory()
            AdvancedLobby.playSound(player, player.location, "cosmetics.disable_cosmetic")
            if (Cosmetics.particles.containsKey(player)) {
                Cosmetics.particles.remove(player)
                player.sendMessage(Locale.COSMETICS_BALLOONS_DISABLE.getMessage(player))
                return
            }
            player.sendMessage(Locale.COSMETICS_BALLOONS_DISABLE_ERROR.getMessage(player))
            return
        }

        if (Cosmetics.balloons.containsKey(player)) {
            Cosmetics.balloons[player]?.remove()
            Cosmetics.balloons.remove(player)
        }
        AdvancedLobby.playSound(player, player.location, "cosmetics.equip_cosmetic")

        when(event.currentItem?.type){
            VMaterial.YELLOW_TERRACOTTA.type ->{
               if (!player.hasPermission("advancedlobby.cosmetics.balloons.yellow")) {
                   player.sendMessage(
                       Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.yellow_balloon.displayname")))
                   return
               }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.YELLOW)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.yellow_balloon.displayname")))

            }

            VMaterial.RED_TERRACOTTA.type ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.red")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.red_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.RED)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.red_balloon.displayname")))

            }

            VMaterial.LIME_TERRACOTTA.type ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.green")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.green_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.GREEN)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.green_balloon.displayname")))

            }

            VMaterial.LIGHT_BLUE_TERRACOTTA.type ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.blue")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.blue_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.BLUE)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.blue_balloon.displayname")))

            }

            Material.HAY_BLOCK ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.hay_block")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.hay_block_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.HAY_BLOCK)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.hay_block_balloon.displayname")))

            }

            Material.SEA_LANTERN ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.sea_lantern")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.sea_lantern_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.SEA_LANTERN)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.sea_lantern_balloon.displayname")))

            }

            Material.BOOKSHELF ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.bookshelf")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.bookshelf_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.BOOKSHELF)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.bookshelf_balloon.displayname")))

            }

            Material.NOTE_BLOCK ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.note_block")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.note_block_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.NOTE_BLOCK)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.note_block_balloon.displayname")))

            }

            else -> {return}
        }

        player.closeInventory()
    }

    private fun cosmeticsGadgetsClick(player: Player,event: InventoryClickEvent){
        if (!validateCosmeticsClick(player,event)) return

        if (event.currentItem?.type == VMaterial.RED_DYE.type) {
            player.closeInventory()
            AdvancedLobby.playSound(player, player.location, "cosmetics.disable_cosmetic")
            if (Cosmetics.gadgets.containsKey(player)) {
                Cosmetics.gadgets.remove(player)

                val noGadget = ItemBuilder(
                    AdvancedLobby.getMaterial("hotbar_items.gadget.unequipped.material"), 1, AdvancedLobby.cfg.getInt("hotbar_items.gadget.unequipped.subside").toShort()).setDisplayName(AdvancedLobby.cfg.getString("hotbar_items.gadget.unequipped.displayname"))
                    .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.gadget.unequipped.lore"))
                player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.gadget.slot"), noGadget)

                player.sendMessage(Locale.COSMETICS_GADGETS_DISABLE.getMessage(player))
                return
            }
            player.sendMessage(Locale.COSMETICS_GADGETS_DISABLE_ERROR.getMessage(player))
            return
        }

        AdvancedLobby.playSound(player, player.location, "cosmetics.equip_cosmetic")

        when(event.currentItem?.type){
            Material.FISHING_ROD->{
                if (!player.hasPermission("advancedlobby.cosmetics.gadgets.grappling_hook")) {
                    player.sendMessage(
                        Locale.COSMETICS_GADGETS_NO_PERMISSION.getMessage(player).replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname")))
                    return
                }

                Cosmetics.equipGadget(player, Cosmetics.GadgetType.GRAPPLING_HOOK)
                player.sendMessage(
                    Locale.COSMETICS_GADGETS_EQUIP.getMessage(player).replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname")))
            }

            Material.FEATHER->{
                if (!player.hasPermission("advancedlobby.cosmetics.gadgets.rocket_jump")) {
                    player.sendMessage(
                        Locale.COSMETICS_GADGETS_NO_PERMISSION.getMessage(player).replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.rocket_jump_gadget.displayname")))
                    return
                }

                Cosmetics.equipGadget(player, Cosmetics.GadgetType.ROCKET_JUMP)
                player.sendMessage(
                    Locale.COSMETICS_GADGETS_EQUIP.getMessage(player).replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.rocket_jump_gadget.displayname")))
            }

            else -> {return}
        }

        player.closeInventory()
    }

    private fun validateCosmeticsClick(player: Player,event: InventoryClickEvent): Boolean{
        if (event.currentItem?.type == Material.BARRIER) {
            CosmeticsInventory.openInventory(player)
            AdvancedLobby.playSound(player, player.location, "cosmetics.change_page")
            return false
        }

        if (event.currentItem?.type == VMaterial.BLACK_STAINED_GLASS_PANE.type) return false
        if (event.currentItem?.type == Material.AIR) return false

        return true
    }



}