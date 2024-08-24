package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.cosmetics.Cosmetics
import de.cyne.advancedlobby.cosmetics.Cosmetics.ParticleType
import de.cyne.advancedlobby.crossversion.VMaterial
import de.cyne.advancedlobby.crossversion.VParticle
import de.cyne.advancedlobby.misc.HiderType
import de.cyne.advancedlobby.misc.LocationManager
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

val JumpPadMaterials = arrayOf(
    VMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.type, 
    VMaterial.DARK_OAK_PRESSURE_PLATE.type, 
    VMaterial.BIRCH_PRESSURE_PLATE.type,
    VMaterial.ACACIA_PRESSURE_PLATE.type, 
    VMaterial.JUNGLE_PRESSURE_PLATE.type, 
    VMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.type, 
    VMaterial.OAK_PRESSURE_PLATE.type, 
    VMaterial.SPRUCE_PRESSURE_PLATE.type, 
    VMaterial.STONE_PRESSURE_PLATE.type)

class PlayerMoveEventListener : Listener {
    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        if (!AdvancedLobby.multiWorld_mode or AdvancedLobby.lobbyWorlds.contains(player.world)) {

            //Display Player Particles
            if (Cosmetics.particles.containsKey(player)) 
                showPlayerParticles(player)
            
            //Player Void TP Check
            if (AdvancedLobby.cfg.getBoolean("void_teleport.enabled")) 
                voidTeleportCheck(player)
            
            //Check if Jump-Pad was triggered
            if (player.location.block.type in JumpPadMaterials && player.location.subtract(0.0, 2.0, 0.0).block.type == Material.REDSTONE_BLOCK) {
                if (AdvancedLobby.cfg.getBoolean("jumppads.enabled")) 
                    activateJumpPad(player)
                
            }

            //Player with shield gadget moves
            if (AdvancedLobby.shield.contains(player)) {
                shieldPlayerMoves(player)
            }
            
            //Check if nearby other players have shield
            shieldOtherPlayerMoves(player)


            //If player is near world border push him back
            if (AdvancedLobby.cfg.getBoolean("worldborder.enabled"))
                pushPlayerBackFromWorldBorder(player)


        }
    }

    private fun pushPlayerBackFromWorldBorder(player: Player) {
        val centerLocation = LocationManager.getLocation(AdvancedLobby.cfg.getString("worldborder.center_location")) ?: return

        if (!playerAtWorldBorder(player, centerLocation))
            return

        if (AdvancedLobby.build.contains(player))
            return

        val centerLocationVector = centerLocation.toVector()
        val playerLocationVector = player.location.toVector()
        val playerVelocityVector = centerLocationVector.clone().subtract(playerLocationVector).normalize().multiply(0.5).setY(0.25)

        player.velocity = playerVelocityVector
        player.playEffect<Any>(player.location, Effect.SMOKE, null)
        AdvancedLobby.playSound(player, player.location, "worldborder_push_back")

        for (otherPlayer in Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer)
                continue

            if (AdvancedLobby.silentLobby.contains(otherPlayer) || AdvancedLobby.silentLobby.contains(player))
                continue

            if (AdvancedLobby.playerHider.containsKey(otherPlayer))
                continue

            otherPlayer.playEffect<Any>(player.location, Effect.SMOKE, null)
            AdvancedLobby.playSound(otherPlayer, player.location, "worldborder_push_back")
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.getInstance(), {
            if (!playerAtWorldBorder(player, centerLocation))
                return@scheduleSyncDelayedTask


            if (AdvancedLobby.build.contains(player))
                return@scheduleSyncDelayedTask


            val location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"))

            if (location != null)
                player.teleport(location)
            }, 100L
        )
    }

    private fun shieldOtherPlayerMoves(player: Player) {
        for (otherPlayer in Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer) 
                continue

            if (!AdvancedLobby.shield.contains(otherPlayer)) 
                continue

            if (player.hasMetadata("NPC") || AdvancedLobby.silentLobby.contains(player) || AdvancedLobby.silentLobby.contains(otherPlayer)) 
                continue

            if (player.hasPermission("advancedlobby.shield.bypass")) 
                continue

            if (player.location.distance(otherPlayer.location) > 2.5) 
                continue
            
            val playerLocationVector = player.location.toVector()
            val otherPlayerLocationVector = otherPlayer.location.toVector()
            val newVelocityVector = playerLocationVector.clone().subtract(otherPlayerLocationVector).normalize().multiply(0.5).setY(0.25)
            player.velocity = newVelocityVector

            player.playEffect<Any>(otherPlayer.location, Effect.ENDER_SIGNAL, null)
            otherPlayer.playEffect<Any>(otherPlayer.location, Effect.ENDER_SIGNAL, null)

            Bukkit.getOnlinePlayers().forEach { otherPlayer2 ->
                if (player === otherPlayer2)
                    return@forEach

                if (AdvancedLobby.silentLobby.contains(player) || AdvancedLobby.silentLobby.contains(otherPlayer))
                    return@forEach

                if (!player.hasPermission("advancedlobby.shield.bypass")) {
                    otherPlayer2.playEffect<Any>(otherPlayer.location, Effect.ENDER_SIGNAL, null)
                }
            }
        }
    }

    private fun shieldPlayerMoves(player: Player) {
        for (entities in player.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (entities !is Player) 
                continue

            if (entities.hasMetadata("NPC") || AdvancedLobby.silentLobby.contains(player) || AdvancedLobby.silentLobby.contains(entities)) 
                continue

            if (entities.hasPermission("advancedlobby.shield.bypass")) 
                continue

            val newPlayerVector = entities.location.toVector()
            val playerLocationVector = player.location.toVector()
            val newVelocityVector = newPlayerVector.clone().subtract(playerLocationVector).normalize().multiply(0.5).setY(0.25)
            entities.velocity = newVelocityVector
            
            player.playEffect<Any>(player.location, Effect.ENDER_SIGNAL, null)

            for (otherPlayer in Bukkit.getOnlinePlayers()) {
                if (player === otherPlayer)
                    continue

                if (AdvancedLobby.silentLobby.contains(player))
                    continue

                if (AdvancedLobby.silentLobby.contains(otherPlayer))
                    continue

                if (entities.hasPermission("advancedlobby.shield.bypass"))
                    continue

                otherPlayer.playEffect<Any>(player.location, Effect.ENDER_SIGNAL, null)
            }
        }
    }

    private fun activateJumpPad(player: Player) {
        val vector = player.location.direction.multiply(AdvancedLobby.cfg.getDouble("jumppads.length"))
            .setY(AdvancedLobby.cfg.getDouble("jumppads.height"))
        
        player.velocity = vector

        AdvancedLobby.playSound(player, player.location, "jumppads")
        player.playEffect<Any>(player.location, Effect.MOBSPAWNER_FLAMES, null)

        for (otherPlayer in Bukkit.getOnlinePlayers()) {
            if (player === otherPlayer)
                continue

            if (AdvancedLobby.silentLobby.contains(player))
                continue

            if (AdvancedLobby.silentLobby.contains(otherPlayer))
                continue

            if (AdvancedLobby.playerHider.containsKey(otherPlayer))
                continue

            AdvancedLobby.playSound(otherPlayer, player.location, "jumppads")
            otherPlayer.playEffect<Any>(player.location, Effect.MOBSPAWNER_FLAMES, null)
        }
    }

    private fun voidTeleportCheck(p: Player) {
        if (p.location.y >= AdvancedLobby.cfg.getDouble("void_teleport.height")) 
            return
        
        val location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"))
        if (location != null) {
            p.teleport(location)
        }
    }

    private fun showPlayerParticles(p: Player) {
        when (Cosmetics.particles[p]) {
            ParticleType.HEART-> showParticles(p, "HEART", 2, 0.3, 0.3, 0.3, 1.0)
            ParticleType.MUSIC -> showParticles(p, "NOTE", 3, 0.3, 0.3, 0.3, 1.0)
            ParticleType.FLAMES -> showParticles(p, "FLAME", 4, 0.0, 0.0, 0.0, 0.1)
            ParticleType.VILLAGER -> showParticles(p, "VILLAGER_HAPPY", 4, 0.5, 0.5, 0.5, 1.0)
            ParticleType.RAINBOW -> showParticles(p, "SPELL_MOB", 8, 0.5, 0.5, 0.5, 1.0)
            null -> return
        }
    }

    private fun showParticles(
        player: Player,
        particle: String,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        extra: Double
    ) {
        VParticle.spawnParticle(player, particle, player.location, count, offsetX, offsetY, offsetZ, extra)
        for (otherPlayer in Bukkit.getOnlinePlayers()) {
            if (otherPlayer === player)
                continue

            if (AdvancedLobby.silentLobby.contains(otherPlayer))
                continue

            if (AdvancedLobby.silentLobby.contains(player))
                continue

            if (AdvancedLobby.playerHider[otherPlayer] != HiderType.NONE) {
                VParticle.spawnParticle(otherPlayer, particle, player.location, count, offsetX, offsetY, offsetZ, extra)
            }
        }
    }


    private fun playerAtWorldBorder(player: Player, location: Location): Boolean {
        val radius = AdvancedLobby.cfg.getDouble("worldborder.radius")
        return (player.location.x > location.x + radius) or (player.location.x < location.x - radius) or (player.location.z > location.z + radius) or (player.location.z < location.z - radius)
    }
}
