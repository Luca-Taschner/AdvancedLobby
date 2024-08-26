package gg.ninjagaming.advancedlobby.misc

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

object VParticle {
    fun getParticleEnum(particle: String): Particle {
        return try {
            Particle.valueOf(particle.uppercase(Locale.getDefault()))
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid particle name: $particle")
        }
    }
}
