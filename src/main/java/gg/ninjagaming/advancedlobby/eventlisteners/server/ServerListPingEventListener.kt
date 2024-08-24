package gg.ninjagaming.advancedlobby.eventlisteners.server

import de.cyne.advancedlobby.AdvancedLobby
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPingEventListener: Listener {
    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        if (!AdvancedLobby.cfg.getBoolean("motd.enabled"))
            return

        val outputString = AdvancedLobby.cfg.getString("motd.first_line") + "\n" + AdvancedLobby.cfg.getString("motd.second_line")
        event.motd(Component.text(outputString))



    }
}