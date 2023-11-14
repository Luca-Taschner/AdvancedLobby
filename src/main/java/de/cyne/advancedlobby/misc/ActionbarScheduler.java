package de.cyne.advancedlobby.misc;

import de.cyne.advancedlobby.AdvancedLobby;
import gg.ninjagaming.advancedlobby.titleapi.TitleApi;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ActionbarScheduler {

    private int taskId;
    private int taskId2;
    private ArrayList<String> messages;
    private int i = 0;

    public ActionbarScheduler(ArrayList<String> messages) {
        this.messages = messages;
    }

    public void start() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvancedLobby.getInstance(), () -> {
            Bukkit.getScheduler().cancelTask(taskId2);
            i++;
            if (i >= messages.size())
                i = 0;

            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players.getWorld() == AdvancedLobby.lobbyWorlds) {
                    taskId2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvancedLobby.getInstance(), () -> {
                        if (AdvancedLobby.placeholderApi) {
                            //TitleAPI.sendActionBar(players, PlaceholderAPI.setPlaceholders(players, messages.get(i)));
                            TitleApi.INSTANCE.sendActionBar(players,PlaceholderAPI.setPlaceholders(players, messages.get(i)));
                            return;
                        }
                        //TitleAPI.sendActionBar(players, ChatColor.translateAlternateColorCodes('&', messages.get(i)));
                        TitleApi.INSTANCE.sendActionBar(players, ChatColor.translateAlternateColorCodes('&', messages.get(i)));
                    }, 0L, 20L);
                }
            }
        }, 0L, AdvancedLobby.cfg.getInt("actionbar.display_time") * 20L);

    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
        Bukkit.getScheduler().cancelTask(taskId2);
    }

}
