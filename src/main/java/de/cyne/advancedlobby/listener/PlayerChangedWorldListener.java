package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.cosmetics.Cosmetics;
import de.cyne.advancedlobby.itembuilder.ItemBuilder;
import de.cyne.advancedlobby.misc.HiderType;
import de.cyne.advancedlobby.misc.LocationManager;
import gg.ninjagaming.advancedlobby.titleapi.TitleApi;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffect;

public class PlayerChangedWorldListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();

        if (AdvancedLobby.multiWorld_mode) {
            if (AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
                double health = AdvancedLobby.cfg.getDouble("player_join.health");
                p.setMaxHealth(health);
                p.setHealth(health);

                p.setFoodLevel(20);

                p.setAllowFlight(false);
                p.setFlying(false);

                p.setFireTicks(0);

                p.setLevel(0);
                p.setExp(0);

                for (PotionEffect effects : p.getActivePotionEffects()) {
                    p.removePotionEffect(effects.getType());
                }

                GameMode gameMode;
                String mode = AdvancedLobby.cfg.getString("player_join.gamemode");
                switch (mode) {
                    case ("0"):
                        gameMode = GameMode.SURVIVAL;
                        break;
                    case ("1"):
                        gameMode = GameMode.CREATIVE;
                        break;
                    case ("2"):
                        gameMode = GameMode.ADVENTURE;
                        break;
                    case ("3"):
                        gameMode = GameMode.SPECTATOR;
                        break;
                    default:
                        gameMode = GameMode.SURVIVAL;
                        break;
                }
                p.setGameMode(gameMode);

                if (AdvancedLobby.cfg.getBoolean("multiworld_mode.clear_inventory")) {
                    p.getInventory().clear();
                    p.getInventory().setArmorContents(null);
                    p.updateInventory();
                }

                if (AdvancedLobby.cfg.getBoolean("title.enabled")) {
                    if (AdvancedLobby.placeholderApi) {
                        TitleApi.INSTANCE.sendTitle(p, 20, 40, 20, AdvancedLobby.getPlaceholderString(p, "title.title"), AdvancedLobby.getPlaceholderString(p, "title.subtitle"));
                    } else {
                        TitleApi.INSTANCE.sendTitle(p, 20, 40, 20, AdvancedLobby.getString("title.title").replace("%player%", AdvancedLobby.getName(p)), AdvancedLobby.getString("title.subtitle").replace("%player%", AdvancedLobby.getName(p)));
                    }
                }

                ItemBuilder teleporter = new ItemBuilder(AdvancedLobby.getMaterial("hotbar_items.teleporter.material"), 1,
                        (short) AdvancedLobby.cfg.getInt("hotbar_items.teleporter.subid")).setDisplayName(
                        ChatColor.translateAlternateColorCodes('&', AdvancedLobby.cfg.getString("hotbar_items.teleporter.displayname")))
                        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.teleporter.lore"));

                if (AdvancedLobby.cfg.getBoolean("hotbar_items.teleporter.enabled")) {
                    p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.teleporter.slot"), teleporter);
                }

                ItemBuilder hider = new ItemBuilder(AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all.material"),
                        1, (short) AdvancedLobby.cfg.getInt("hotbar_items.player_hider.show_all.subid"))
                        .setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                AdvancedLobby.cfg.getString("hotbar_items.player_hider.show_all.displayname")))
                        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.player_hider.show_all.lore"));

                if (AdvancedLobby.cfg.getBoolean("hotbar_items.player_hider.enabled")) {
                    p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.player_hider.slot"), hider);
                }

                ItemBuilder cosmetics = new ItemBuilder(AdvancedLobby.getMaterial("hotbar_items.cosmetics.material"), 1,
                        (short) AdvancedLobby.cfg.getInt("hotbar_items.cosmetics.subid")).setDisplayName(
                        ChatColor.translateAlternateColorCodes('&', AdvancedLobby.cfg.getString("hotbar_items.cosmetics.displayname")))
                        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.cosmetics.lore"));

                if (AdvancedLobby.cfg.getBoolean("hotbar_items.cosmetics.enabled")) {
                    p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.cosmetics.slot"), cosmetics);
                }

                ItemBuilder no_gadget = new ItemBuilder(AdvancedLobby.getMaterial("hotbar_items.gadget.unequipped.material"), 1,
                        (short) AdvancedLobby.cfg.getInt("hotbar_items.gadget.unequipped.subid")).setDisplayName(
                        ChatColor.translateAlternateColorCodes('&', AdvancedLobby.cfg.getString("hotbar_items.gadget.unequipped.displayname")))
                        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.gadget.unequipped.lore"));

                if (AdvancedLobby.cfg.getBoolean("hotbar_items.gadget.enabled")) {
                    p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.gadget.slot"), no_gadget);
                }

                ItemBuilder silentlobby = new ItemBuilder(
                        AdvancedLobby.getMaterial("hotbar_items.silentlobby.deactivated.material"), 1,
                        (short) AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.deactivated.subid"))
                        .setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                AdvancedLobby.cfg.getString("hotbar_items.silentlobby.deactivated.displayname")))
                        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.silentlobby.deactivated.lore"));

                if (AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.enabled") && p.hasPermission("advancedlobby.silentlobby")) {
                    p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.silentlobby.slot"), silentlobby);
                }

                ItemBuilder shield = new ItemBuilder(
                        AdvancedLobby.getMaterial("hotbar_items.shield.deactivated.material"), 1,
                        (short) AdvancedLobby.cfg.getInt("hotbar_items.shield.deactivated.subid"))
                        .setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                AdvancedLobby.cfg.getString("hotbar_items.shield.deactivated.displayname")))
                        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.deactivated.lore"));

                if (AdvancedLobby.cfg.getBoolean("hotbar_items.shield.enabled") && p.hasPermission("advancedlobby.shield")) {
                    p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.shield.slot"), shield);
                }

                ItemBuilder custom_item = new ItemBuilder(
                        AdvancedLobby.getMaterial("hotbar_items.custom_item.material"), 1,
                        (short) AdvancedLobby.cfg.getInt("hotbar_items.custom_item.subid"))
                        .setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                AdvancedLobby.cfg.getString("hotbar_items.custom_item.displayname")))
                        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.custom_item.lore"));

                if (AdvancedLobby.cfg.getBoolean("hotbar_items.custom_item.enabled") && p.hasPermission("advancedlobby.custom_item")) {
                    p.getInventory().setItem(AdvancedLobby.cfg.getInt("hotbar_items.custom_item.slot"), custom_item);
                }

                for (Player players : AdvancedLobby.playerHider.keySet()) {
                    if (AdvancedLobby.playerHider.get(players) == HiderType.VIP) {
                        if (!p.hasPermission("advancedlobby.player_hider.bypass")) {
                            players.hidePlayer(p);
                        }
                    }
                    if (AdvancedLobby.playerHider.get(players) == HiderType.NONE) {
                        players.hidePlayer(p);
                    }
                }

                for (Player players : AdvancedLobby.silentLobby) {
                    players.hidePlayer(p);
                    p.hidePlayer(players);
                }

                if (AdvancedLobby.cfg.getBoolean("player_join.join_at_spawn")) {
                    Location location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"));
                    if (location != null) {
                        p.teleport(location);
                    }
                }
                if (AdvancedLobby.updateAvailable && p.hasPermission("advancedlobby.admin")) {
                    TextComponent message = new TextComponent("§8┃ §bAdvancedLobby §8┃ §7Download now §8▶ ");
                    TextComponent extra = new TextComponent("§8*§aclick§8*");

                    extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §7Redirect to §bhttps://spigotmc.org/").create()));
                    extra.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://spigotmc.org/resources/35799/"));

                    message.addExtra(extra);


                    p.sendMessage("");
                    p.sendMessage("§8┃ §bAdvancedLobby §8┃ §7A §anew update §7for §bAdvancedLobby §7was found§8.");
                    p.spigot().sendMessage(message);
                    p.sendMessage("");
                }
            }

            if (AdvancedLobby.lobbyWorlds.contains(e.getFrom()) && !AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
                p.setMaxHealth(20);
                if (Cosmetics.balloons.containsKey(p)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.getInstance(), () -> Cosmetics.balloons.get(p).remove(), 5L);
                }
                AdvancedLobby.build.remove(p);
                AdvancedLobby.buildInventory.remove(p);
                AdvancedLobby.fly.remove(p);
                AdvancedLobby.playerHider.remove(p);
                AdvancedLobby.shield.remove(p);
                AdvancedLobby.silentLobby.remove(p);

                Cosmetics.hats.remove(p);
                Cosmetics.gadgets.remove(p);

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!AdvancedLobby.silentLobby.contains(players)) {
                        p.showPlayer(players);
                    }
                }

                if (AdvancedLobby.cfg.getBoolean("multiworld_mode.clear_inventory")) {
                    p.getInventory().clear();
                    p.getInventory().setArmorContents(null);
                    p.updateInventory();
                }

            }
        }
    }
}
