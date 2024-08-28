package gg.ninjagaming.advancedlobby.eventlisteners.player

import gg.ninjagaming.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.misc.Locale
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
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


        if (!AdvancedLobby.cfg.getBoolean("chat_format.enabled"))
            return

        if (player.hasPermission("advancedlobby.chatcolor")) {
            message = ChatColor.translateAlternateColorCodes('&', message)
        }

        val format = AdvancedLobby.getString("chat_format.format").replace("%player%", AdvancedLobby.getName(event.player))

        message = format.replace("%message%", message)
        val messageComponent = Component.text(message).asComponent()
        val audience = Audience.audience(event.viewers())
        val customRenderer = CustomRenderer()
        val renderedMessage = customRenderer.render(player, Component.text(""), messageComponent, audience)
        Bukkit.broadcast(renderedMessage)
        event.isCancelled = true
    }
}

class CustomRenderer : ChatRenderer {
    override fun render(
        player: Player,
        header: Component,
        footer: Component,
        audience: Audience
    ): Component {
        return footer
    }
}
