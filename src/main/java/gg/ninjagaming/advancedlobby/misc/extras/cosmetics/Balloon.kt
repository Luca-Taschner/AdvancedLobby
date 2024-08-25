package gg.ninjagaming.advancedlobby.misc.extras.cosmetics

import org.bukkit.Material
import org.bukkit.entity.Bat
import org.bukkit.entity.EntityType
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Balloon {
    private var player: Player
    private var material: Material
    var fallingBlock: FallingBlock? = null
        private set
    var bat: Bat? = null
        private set

    constructor(player: Player, material: Material) {
        this.player = player
        this.material = material
    }

    constructor(player: Player, itemStack: ItemStack) {
        this.player = player
        this.material = itemStack.type
    }

    fun create() {
        val location = player.location
        location.yaw += 90.0f
        location.pitch = -45.0f
        val direction = location.direction.normalize()
        location.add(direction.x * 1.5, direction.y * 1.5 + 0.5, direction.z * 1.5)

        this.bat = player.world.spawnEntity(location, EntityType.BAT) as Bat

        this.fallingBlock = player.world.spawn(location, FallingBlock::class.java){
            it.blockData = material.createBlockData()
        }

        fallingBlock!!.dropItem = false

        bat!!.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 255))
        bat!!.canPickupItems = false
        bat!!.setLeashHolder(player)
        bat!!.addPassenger(fallingBlock!!)
        bat!!.removeWhenFarAway = false

        bats[player] = bat!!
        fallingBlocks[player] = fallingBlock!!
    }

    fun remove() {
        if (fallingBlocks.containsKey(player)) {
            fallingBlocks[player]!!.remove()
            fallingBlocks.remove(player)
        }
        if (bats.containsKey(player)) {
            bats[player]!!.remove()
            bats.remove(player)
        }
    }

    companion object {
        var fallingBlocks: HashMap<Player, FallingBlock> = HashMap()
        var bats: HashMap<Player, Bat> = HashMap()
    }
}
