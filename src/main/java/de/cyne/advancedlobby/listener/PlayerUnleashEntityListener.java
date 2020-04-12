package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class PlayerUnleashEntityListener implements Listener {

    @EventHandler
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
        Player p = e.getPlayer();
        if (!AdvancedLobby.singleWorld_mode | p.getWorld() == AdvancedLobby.lobbyWorld) {
            if (!AdvancedLobby.build.contains(p)) {
                e.setCancelled(true);
            }
        }
    }

}
