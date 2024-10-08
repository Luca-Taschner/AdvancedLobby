package gg.ninjagaming.advancedlobby.eventlisteners.inventory

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.extras.cosmetics.Cosmetics
import gg.ninjagaming.advancedlobby.inventorybuilder.*
import gg.ninjagaming.advancedlobby.misc.ItemBuilder
import gg.ninjagaming.advancedlobby.misc.Locale
import gg.ninjagaming.advancedlobby.misc.LocationManager
import gg.ninjagaming.advancedlobby.misc.SilentLobby
import gg.ninjagaming.advancedlobby.misc.VParticle.getParticleEnum
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
            AdvancedLobby.instance!!.config.getConfigurationSection("inventories.teleporter.items")?.getKeys(false)
                ?: return

        if (AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.enabled") && !AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.inHotbar") && player.hasPermission("advancedlobby.silentlobby")){
            if (AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.slot") == event.slot){
                player.closeInventory()
                if (AdvancedLobby.silentLobby.contains(player)){
                    SilentLobby.removePlayer(player)
                    CompassInventory.openInventory(player)
                    return
                }

                SilentLobby.addPlayer(player)
                CompassInventory.openInventory(player)
                return
            }
        }

        configurationSectionKeys.forEach{
            val material = AdvancedLobby.getMaterial("inventories.teleporter.items.$it.material")

            if (event.currentItem?.type != material)
                return@forEach


            val itemExecutionNode = "inventories.teleporter.items.$it.execute"

            val itemExecutionKeys = AdvancedLobby.cfg.getConfigurationSection(itemExecutionNode)?.getKeys(false)


            if (itemExecutionKeys == null)
            {
                if(player.hasPermission("advancedlobby.admin"))
                    player.sendMessage(Locale.COMPASS_EXEC_NOT_FOUND.getMessage(player))

                val locationString = AdvancedLobby.cfg.getString("inventories.teleporter.items.$it.location")?: "spawn"
                locationExecution(player,locationString)

                return@forEach

            }

            player.closeInventory()

            handleMessageExecutions(player, itemExecutionKeys, itemExecutionNode)
        }
    }

    private fun cosmeticsClick(player: Player, event: InventoryClickEvent) {

        when(event.currentItem?.type){
            Material.PUMPKIN -> CosmeticsHatsInventory.openInventory(player)
            Material.BLAZE_POWDER -> CosmeticsParticlesInventory.openInventory(player)
            Material.LEAD ->  CosmeticsBalloonsInventory.openInventory(player)
            Material.FISHING_ROD -> CosmeticsGadgetsInventory.openInventory(player)

            else -> {return}
        }
        AdvancedLobby.playSound(player, player.location, "cosmetics.change_page")

    }

    private fun cosmeticsHatClick(player: Player,event: InventoryClickEvent){
        if (!validateCosmeticsClick(player,event)) return

        if(event.currentItem?.type == Material.RED_DYE){
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

        if (event.currentItem?.type == Material.RED_DYE) {
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

            Material.MUSIC_DISC_STRAD->{
                if (!player.hasPermission("advancedlobby.cosmetics.particles.music")){
                    player.sendMessage(
                        Locale.COSMETICS_PARTICLES_NO_PERMISSION.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.music_particles.displayname")))
                    return
                }

                Cosmetics.particles[player] = Cosmetics.ParticleType.MUSIC
                player.sendMessage(
                    Locale.COSMETICS_PARTICLES_EQUIP.getMessage(player).replace("%particles%", AdvancedLobby.getString("inventories.cosmetics_particles.music_particles.displayname")))
            }

            Material.FIRE_CHARGE->{
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

        if (event.currentItem?.type == Material.RED_DYE) {
            player.closeInventory()
            AdvancedLobby.playSound(player, player.location, "cosmetics.disable_cosmetic")
            if (Cosmetics.balloons.containsKey(player)) {
                Cosmetics.balloons[player]?.removeBalloon()
                Cosmetics.balloons.remove(player)
                player.sendMessage(Locale.COSMETICS_BALLOONS_DISABLE.getMessage(player))
                return
            }
            player.sendMessage(Locale.COSMETICS_BALLOONS_DISABLE_ERROR.getMessage(player))
            return
        }

        if (Cosmetics.balloons.containsKey(player)) {
            Cosmetics.balloons[player]?.removeBalloon()
            Cosmetics.balloons.remove(player)


        }
        AdvancedLobby.playSound(player, player.location, "cosmetics.equip_cosmetic")

        when(event.currentItem?.type){
            Material.YELLOW_TERRACOTTA ->{
               if (!player.hasPermission("advancedlobby.cosmetics.balloons.yellow")) {
                   player.sendMessage(
                       Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.yellow_balloon.displayname")))
                   return
               }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.YELLOW)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.yellow_balloon.displayname")))

            }

            Material.RED_TERRACOTTA ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.red")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.red_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.RED)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.red_balloon.displayname")))

            }

            Material.LIME_TERRACOTTA ->{
                if (!player.hasPermission("advancedlobby.cosmetics.balloons.green")) {
                    player.sendMessage(
                        Locale.COSMETICS_BALLOONS_NO_PERMISSION.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.green_balloon.displayname")))
                    return
                }

                Cosmetics.equipBalloon(player, Cosmetics.BalloonType.GREEN)
                player.sendMessage(
                    Locale.COSMETICS_BALLOONS_EQUIP.getMessage(player).replace("%balloon%", AdvancedLobby.getString("inventories.cosmetics_balloons.green_balloon.displayname")))

            }

            Material.LIGHT_BLUE_TERRACOTTA ->{
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

        if (event.currentItem?.type == Material.RED_DYE) {
            player.closeInventory()
            AdvancedLobby.playSound(player, player.location, "cosmetics.disable_cosmetic")
            if (Cosmetics.gadgets.containsKey(player)) {
                Cosmetics.gadgets.remove(player)

                val noGadget = ItemBuilder(
                    AdvancedLobby.getMaterial("hotbar_items.gadget.unequipped.material"), 1).setDisplayName(
                    AdvancedLobby.cfg.getString("hotbar_items.gadget.unequipped.displayname"))
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

            Material.ENDER_EYE->{
                if (!player.hasPermission("advancedlobby.cosmetics.gadgets.ender_pearl")) {
                    player.sendMessage(
                        Locale.COSMETICS_GADGETS_NO_PERMISSION.getMessage(player).replace("%gadget%", AdvancedLobby.getString("hotbar_items.shield.menu_item.displayname")))
                    return
                }

                Cosmetics.equipGadget(player, Cosmetics.GadgetType.SHIELD)
                player.sendMessage(
                    Locale.COSMETICS_GADGETS_EQUIP.getMessage(player).replace("%gadget%", AdvancedLobby.getString("hotbar_items.shield.menu_item.displayname")))
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

        if (event.currentItem?.type == Material.BLACK_STAINED_GLASS_PANE) return false
        if (event.currentItem?.type == Material.AIR) return false

        return true
    }

    private fun handleMessageExecutions(player: Player, itemExecutions: Set<String>, currentItemExecutionNode: String){
        itemExecutions.forEach{

            val executionSubNodes = AdvancedLobby.cfg.getConfigurationSection("$currentItemExecutionNode.$it")?.getValues(false)?.toList() ?: return@forEach

            executionSubNodes.forEach{ pairIt: Pair<String, Any> ->
                when(pairIt.first){
                    "message" -> messageExecution(player, pairIt.second.toString())
                    "command" -> commandExecution(player, pairIt.second.toString())
                    "console_command" -> consoleCommandExecution(player, pairIt.second.toString())
                    "location" -> locationExecution(player, AdvancedLobby.cfg.getString("inventories.teleporter.items.$it.location")?: "spawn")
                    "delay" -> delayExecution(pairIt.second as Int)
                    "server" -> serverExecution(player, pairIt.second.toString())
                }
            }
        }
    }

    private fun messageExecution(player: Player, message: String){
        message.replace("%player%", player.name)
        player.sendMessage(message)
    }

    private fun commandExecution(player: Player, command: String){
        command.replace("%player%", player.name)
        player.performCommand(command)
    }

    private fun consoleCommandExecution(player: Player, command: String){
        command.replace("%player%", player.name)
        AdvancedLobby.instance!!.server.dispatchCommand(Bukkit.getConsoleSender(), command)
    }

    private fun locationExecution(player: Player, locationString: String){
        val location= LocationManager.getLocation(locationString)

        if (location == null){
            if(player.hasPermission("advancedlobby.admin")) {
                player.sendMessage(
                    Locale.COMPASS_LOC_NOT_FOUND_ADMIN.getMessage(player).replace(
                        "%location%",
                        locationString
                    ))
                return
            }
            player.sendMessage(
                Locale.COMPASS_LOC_NOT_FOUND.getMessage(player).replace(
                    "%location%",
                    locationString
                )
            )
            return
        }

        player.teleport(location)
        AdvancedLobby.playSound(player, player.location, "teleporter.teleport")
        player.spawnParticle(getParticleEnum("SPELL_WITCH"), location, 64, 0.0, 0.0, 0.0, 0.1)

        val players = Bukkit.getOnlinePlayers()

        players.forEach {itPlayer: Player ->
            if (itPlayer == player)
                return@forEach

            if(AdvancedLobby.playerHider.containsKey(itPlayer) || AdvancedLobby.silentLobby.contains(itPlayer) || AdvancedLobby.silentLobby.contains(player))
                return@forEach

            itPlayer.spawnParticle(getParticleEnum("SPELL_WITCH"), location, 64, 0.0, 0.0, 0.0, 0.1)
        }

    }

    private fun delayExecution(delay: Int){
        Thread.sleep(delay.toLong()*500)
    }

    private fun serverExecution(player: Player, server: String){
        try {
            val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
            out.writeUTF("Connect")
            out.writeUTF(server)

            player.sendPluginMessage(AdvancedLobby.instance!!, "BungeeCord", out.toByteArray())
        }catch (e: Exception){
            println(e)}
    }



}