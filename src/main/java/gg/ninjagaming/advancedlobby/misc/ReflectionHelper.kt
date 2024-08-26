package gg.ninjagaming.advancedlobby.misc


import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun <T : Enum<T>> enumValueOf(enumClass: Class<T>, enumName: String): T {
    return java.lang.Enum.valueOf(enumClass, enumName.uppercase())
}

fun getOBCClass(name: String): Class<*>? {
    val version =
        Bukkit.getServer().javaClass.getPackage().name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[3]
    try {
        return Class.forName("org.bukkit.craftbukkit.$version.$name")
    } catch (ex: ClassNotFoundException) {
        ex.printStackTrace()
    }
    return null
}

fun getNMSClass(name: String): Class<*>? {
    val version =
        Bukkit.getServer().javaClass.getPackage().name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[3]
    try {
        return Class.forName("net.minecraft.server.$version.$name")
    } catch (ex: ClassNotFoundException) {
        ex.printStackTrace()
    }
    return null
}

fun sendPacket(player: Player, packet: Any) {
    try {
        val handle = player.javaClass.getMethod("getHandle", *arrayOfNulls(0)).invoke(player, *arrayOfNulls(0))
        val playerConnection = handle.javaClass.getField("playerConnection")[handle]
        playerConnection.javaClass.getMethod("sendPacket", *arrayOf(getNMSClass("Packet")))
            .invoke(playerConnection, *arrayOf(packet))
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}