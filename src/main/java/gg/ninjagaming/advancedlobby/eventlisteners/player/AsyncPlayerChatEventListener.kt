package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.Locale
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AsyncPlayerChatEventListener : Listener {
    @EventHandler
    fun onAsyncPlayerChat(event: AsyncChatEvent) {
        val player = event.player

        for (silentPlayers in AdvancedLobby.silentLobby) {
            event.viewers().remove(silentPlayers)
        }

        if (AdvancedLobby.silentLobby.contains(player)) {
            event.isCancelled = true
            player.sendMessage(Locale.SILENTLOBBY_CHAT_BLOCKED.getMessage(player))
        }

        if (AdvancedLobby.globalMute) {
            if (!player.hasPermission("advancedlobby.globalmute.bypass")) {
                event.isCancelled = true
                player.sendMessage(Locale.GLOBALMUTE_CHAT_BLOCKED.getMessage(player))
            }
        }

        var message = LegacyComponentSerializer.legacySection().serialize(event.message()).replace("%", "%%")


        if (AdvancedLobby.cfg.getBoolean("chat_format.enabled")) {
            if (player.hasPermission("advancedlobby.chatcolor")) {
                message = ChatColor.translateAlternateColorCodes('&', message)
            }

            val format = AdvancedLobby.getString("chat_format.format").replace("%player%", AdvancedLobby.getName(event.player))

            message = String.format(format, message)
            event.message(Component.text(message))
        }
    }
}
