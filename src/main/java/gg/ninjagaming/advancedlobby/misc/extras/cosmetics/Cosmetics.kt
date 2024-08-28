package gg.ninjagaming.advancedlobby.misc.extras.cosmetics

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.itembuilders.ShieldItemBuilder.itemStackShieldActivate
import gg.ninjagaming.advancedlobby.misc.ItemBuilder
import gg.ninjagaming.advancedlobby.misc.Locale
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Cosmetics {
    var hats: HashMap<Player, HatType> = HashMap()
    var particles: HashMap<Player, ParticleType> = HashMap()
    @JvmField
    var balloons: HashMap<Player, Balloon> = HashMap()
    var gadgets: HashMap<Player, GadgetType> = HashMap()
    private var gadgetReloading: ArrayList<Player> = ArrayList()

    fun equipHat(player: Player, type: HatType) {
        if (hats.containsKey(player)) {
            hats.remove(player)
        }
        var hat: ItemStack? = null
        var message: String? = null
        when (type) {
            HatType.MELON_BLOCK, HatType.MELON -> {
                if (player.hasPermission("advancedlobby.cosmetics.hats.melon")) {
                    hat =
                        ItemBuilder(Material.MELON).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.melon_hat.displayname"))
                    hats[player] = HatType.MELON
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.melon_hat.displayname"))

                }
                else
                    message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.melon_hat.displayname"))
            }

            HatType.TNT -> {
                if (player.hasPermission("advancedlobby.cosmetics.hats.tnt")) {
                    hat =
                        ItemBuilder(Material.TNT).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.tnt_hat.displayname"))
                    hats[player] = HatType.TNT
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.tnt_hat.displayname"))

                }
                else
                    message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.tnt_hat.displayname"))
            }

            HatType.GLASS -> {
                if (player.hasPermission("advancedlobby.cosmetics.hats.glass")) {
                    hat =
                        ItemBuilder(Material.GLASS).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.glass_hat.displayname"))
                    hats[player] = HatType.GLASS
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.glass_hat.displayname"))

                }
                else
                    message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.glass_hat.displayname"))
            }

            HatType.SPONGE -> {
                if (player.hasPermission("advancedlobby.cosmetics.hats.sponge")) {
                    hat =
                        ItemBuilder(Material.SPONGE).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.sponge_hat.displayname"))
                    hats[player] = HatType.SPONGE
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.sponge_hat.displayname"))

                }
                else
                    message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.sponge_hat.displayname"))
            }

            HatType.PUMPKIN -> {
                if (player.hasPermission("advancedlobby.cosmetics.hats.pumpkin")) {
                    hat =
                        ItemBuilder(Material.PUMPKIN).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.pumpkin_hat.displayname"))
                    hats[player] = HatType.PUMPKIN
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.pumpkin_hat.displayname"))

                }
                else
                    message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.pumpkin_hat.displayname"))
            }

            HatType.CACTUS -> {
                if (player.hasPermission("advancedlobby.cosmetics.hats.cactus")) {
                    hat =
                        ItemBuilder(Material.CACTUS).setDisplayName(AdvancedLobby.getString("inventories.cosmetics_hats.cactus_hat.displayname"))
                    hats[player] = HatType.CACTUS
                    message = Locale.COSMETICS_HATS_EQUIP.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.cactus_hat.displayname"))

                }
                else
                    message = Locale.COSMETICS_HATS_NO_PERMISSION.getMessage(player)
                        .replace("%hat%", AdvancedLobby.getString("inventories.cosmetics_hats.cactus_hat.displayname"))
            }
        }
        player.inventory.helmet = hat
        player.sendMessage(message)
    }

    fun equipBalloon(player: Player, type: BalloonType) {
        val balloon: Balloon = when (type) {
            BalloonType.YELLOW -> Balloon(player, Material.YELLOW_TERRACOTTA)
            BalloonType.RED -> Balloon(player, Material.RED_TERRACOTTA)
            BalloonType.GREEN -> Balloon(player, Material.LIME_TERRACOTTA)
            BalloonType.BLUE -> Balloon(player, Material.LIGHT_BLUE_TERRACOTTA)
            BalloonType.HAY_BLOCK -> Balloon(player, Material.HAY_BLOCK)
            BalloonType.SEA_LANTERN -> Balloon(player, Material.SEA_LANTERN)
            BalloonType.BOOKSHELF -> Balloon(player, Material.BOOKSHELF)
            BalloonType.NOTE_BLOCK -> Balloon(player, Material.NOTE_BLOCK)
        }

        if (!AdvancedLobby.silentLobby.contains(player)) {

            balloon.createBalloon()
        }
        balloons[player] = balloon

    }

    fun equipGadget(player: Player, type: GadgetType) {
        val gadget: ItemStack = when (type) {
            GadgetType.GRAPPLING_HOOK -> ItemBuilder(Material.FISHING_ROD).setDisplayName(
                AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace(
                    "%gadget%",
                    AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname")
                )
            ).setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.gadget.equipped.lore"))

            GadgetType.ROCKET_JUMP -> ItemBuilder(Material.FEATHER).setDisplayName(
                AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace(
                    "%gadget%",
                    AdvancedLobby.getString("inventories.cosmetics_gadgets.rocket_jump_gadget.displayname")
                )
            ).setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.gadget.equipped.lore"))

            GadgetType.SHIELD -> itemStackShieldActivate
        }
        player.inventory.setItem(AdvancedLobby.cfg.getInt("hotbar_items.gadget.slot"), gadget)
        gadgets[player] = type
    }

    @JvmStatic
    fun startBalloonTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            AdvancedLobby.instance!!,
            {
                for (players in Bukkit.getOnlinePlayers()) {
                    if (!balloons.containsKey(players))
                        continue

                    if (!(!AdvancedLobby.multiWorld_mode or (players.world === AdvancedLobby.lobbyWorlds)))
                        continue

                    if (AdvancedLobby.silentLobby.contains(players))
                        continue

                    if (balloons[players]!!.fallingBlock == null) {
                        balloons[players]!!.createBalloon()
                    }
                    if (balloons[players]!!.fallingBlock!!.isDead or balloons[players]!!.bat!!.isDead) {
                        balloons[players]!!.removeBalloon()
                        balloons[players]!!.createBalloon()
                    }
                    val localBat = balloons[players]!!.bat

                    val location = players.location
                    location.yaw += 90.0f
                    location.pitch = -45.0f

                    val direction = location.direction.normalize()
                    location.add(direction.x * 1.5, direction.y * 1.5 + 0.5, direction.z * 1.5)

                    val locationVector = location.toVector()
                    val batVector =
                        balloons[players]!!.bat!!.location.toVector()

                    localBat!!.velocity = locationVector.clone().subtract(batVector).multiply(0.5)
                }
            }, 0L, 3L
        )
    }

    fun reloadGadget(player: Player) {
        gadgetReloading.add(player)
        Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.instance!!, {
            if (player.isOnline) {
                gadgetReloading.remove(player)
            }
        }, 100L)
    }

    enum class HatType {
        MELON_BLOCK, MELON, TNT, GLASS, SPONGE, PUMPKIN, CACTUS
    }

    enum class ParticleType {
        HEART, MUSIC, FLAMES, VILLAGER, RAINBOW
    }

    enum class BalloonType {
        YELLOW, RED, GREEN, BLUE, HAY_BLOCK, SEA_LANTERN, BOOKSHELF, NOTE_BLOCK
    }

    enum class GadgetType {
        GRAPPLING_HOOK, ROCKET_JUMP, SHIELD
    }
}
