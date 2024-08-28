package gg.ninjagaming.advancedlobby.misc

import gg.ninjagaming.advancedlobby.AdvancedLobby
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.IOException

object LocationManager {
    fun saveLocation(location: Location, name: String) {
        val world = location.world.name
        val x = location.x
        val y = location.y
        val z = location.z
        val pitch = location.pitch
        val yaw = location.yaw

        val compact = "$world:$x;$y;$z;$pitch;$yaw"

        AdvancedLobby.cfgL[name] = compact

        try {
            AdvancedLobby.cfgL.save(AdvancedLobby.fileLocations)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun deleteLocation(name: String?) {
        if (name == null)
            return

        if (AdvancedLobby.cfgL.isSet(name)) {
            AdvancedLobby.cfgL[name] = null
            try {
                AdvancedLobby.cfgL.save(AdvancedLobby.fileLocations)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getLocation(name: String?): Location? {
        if (name == null)
            return null

        if (AdvancedLobby.cfgL.isSet(name)) {
            val compact = AdvancedLobby.cfgL.getString(name)

            val world = compact!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val x = compact.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toDouble()
            val y = compact.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toDouble()
            val z = compact.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2].toDouble()
            val pitch = compact.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3].toFloat()
            val yaw = compact.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[4].toFloat()

            val location = Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
            return location
        }
        return null
    }
}