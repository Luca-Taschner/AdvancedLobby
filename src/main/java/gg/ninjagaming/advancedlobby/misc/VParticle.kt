package gg.ninjagaming.advancedlobby.misc

import org.bukkit.Particle

import java.util.Locale

object VParticle {


    fun getParticleEnum(particle: String): Particle {
        return try {
            Particle.valueOf(particle.uppercase(Locale.getDefault()))
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid particle name: $particle")
        }
    }
}
