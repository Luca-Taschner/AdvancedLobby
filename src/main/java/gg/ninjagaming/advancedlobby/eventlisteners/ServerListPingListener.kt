package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPingListener: Listener {
    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        if (!AdvancedLobby.cfg.getBoolean("motd.enabled"))
            return

        val outputString = AdvancedLobby.cfg.getString("motd.first_line") + "\n" + AdvancedLobby.cfg.getString("motd.second_line")
        event.motd = outputString



    }
}