package gg.ninjagaming.advancedlobby.eventlisteners

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class SignChangeEventListener :Listener {
    @EventHandler
    fun onSignChange(event : SignChangeEvent) {
        val player: Player = event.player
        val signLines = event.lines()

        if (!player.hasPermission("advancedlobby.admin"))
            return

        for (i in 0..3) {
            val plainTextSerializedMessage = PlainTextComponentSerializer.plainText().serialize(signLines[i])
            val legacyDeserializedMessage = LegacyComponentSerializer.legacy('&').deserialize(plainTextSerializedMessage)
            event.line(i,legacyDeserializedMessage)
        }
    }
}