package de.cyne.advancedlobby;

import de.cyne.advancedlobby.commands.*;
import de.cyne.advancedlobby.cosmetics.Cosmetics;
import de.cyne.advancedlobby.listener.*;
import de.cyne.advancedlobby.metrics.Metrics;
import de.cyne.advancedlobby.misc.HiderType;
import de.cyne.advancedlobby.misc.Updater;
import gg.ninjagaming.advancedlobby.eventlisteners.*;
import gg.ninjagaming.advancedlobby.runnables.ActionBarRunnable;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdvancedLobby extends JavaPlugin {

    private static AdvancedLobby instance;

    public static File file = new File("plugins/AdvancedLobby", "config.yml");
    public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public static File fileLocations = new File("plugins/AdvancedLobby", "locations.yml");
    public static FileConfiguration cfgL = YamlConfiguration.loadConfiguration(fileLocations);

    public static File fileMessages = new File("plugins/AdvancedLobby", "messages.yml");
    public static FileConfiguration cfgM = YamlConfiguration.loadConfiguration(fileMessages);

    public static File fileSounds = new File("plugins/AdvancedLobby", "sounds.yml");
    public static FileConfiguration cfgS = YamlConfiguration.loadConfiguration(fileSounds);

    public static ArrayList<String> actionbarMessages = new ArrayList<>();
    public static ArrayList<Player> build = new ArrayList<>();
    public static ArrayList<Player> fly = new ArrayList<>();
    public static ArrayList<Player> shield = new ArrayList<>();
    public static ArrayList<Player> silentLobby = new ArrayList<>();
    public static HashMap<Player, ItemStack[]> buildInventory = new HashMap<>();
    public static HashMap<Player, HiderType> playerHider = new HashMap<>();

    public static boolean globalMute = false;
    public static boolean updateAvailable = false;
    public static boolean placeholderApi = false;

    public static boolean multiWorld_mode;

    public static ArrayList<World> lobbyWorlds = new ArrayList<>();
    public static Updater updater;

    public static HashMap<String, ErrorType> errors = new HashMap<>();

    public Metrics metrics;

    public static BukkitTask actionBarRunnable;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.createFiles();
        this.loadFiles();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            AdvancedLobby.getInstance().log("PlaceholderAPI was found. Connected.");
            placeholderApi = true;
        }

        updater = new Updater(35799);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvancedLobby.getInstance(), () -> {
            updater.run();
        }, 0L, 20 * 60 * 60 * 24); //once a day

        if (cfg.getBoolean("actionbar.enabled")) {
            actionbarMessages.addAll(AdvancedLobby.cfg.getStringList("actionbar.messages"));
            this.prepareActionBarRunnable();
        }

        multiWorld_mode = AdvancedLobby.cfg.getBoolean("multiworld_mode.enabled");

        for (World world : Bukkit.getWorlds()) {
            if (AdvancedLobby.cfg.getStringList("lobby_worlds").contains(world.getName())) {
                lobbyWorlds.add(world);
            }
        }

        this.prepareLobbyWorlds();

        this.registerCommands();
        this.registerListener();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Cosmetics.startBalloonTask();

        metrics = new Metrics(AdvancedLobby.getInstance(), 7014);
        metrics.addCustomChart(new Metrics.SimplePie("singleworld_mode", () -> multiWorld_mode ? "enabled" : "disabled"));
    }

    public static void prepareActionBarRunnable()
    {
        actionBarRunnable = AdvancedLobby.getInstance().getServer().getScheduler()
                .runTaskTimer(AdvancedLobby.getInstance(), ActionBarRunnable.INSTANCE.schedulingRunnable(actionbarMessages,cfg.getInt("actionbar.display_time")), 0L, cfg.getInt("actionbar.display_time") * 20L* actionbarMessages.size());
    }

    @Override
    public void onDisable() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (Cosmetics.balloons.containsKey(players)) {
                Cosmetics.balloons.get(players).remove();
            }
        }
    }

    private void prepareLobbyWorlds() {
        for (World world : multiWorld_mode ? AdvancedLobby.lobbyWorlds : Bukkit.getWorlds()) {
            String weatherType = AdvancedLobby.cfg.getString("weather.weather_type").toUpperCase();
            switch (weatherType) {
                case ("CLEAR"):
                    world.setStorm(false);
                    world.setThundering(false);
                    break;
                case ("RAIN"):
                    world.setStorm(true);
                    world.setThundering(false);
                    break;
                case ("THUNDER"):
                    world.setStorm(true);
                    world.setThundering(true);
                    break;
            }

            if (!AdvancedLobby.cfg.getBoolean("weather.enabled"))
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }
    }

    private void registerCommands() {
        AdvancedLobby.getInstance().getCommand("advancedlobby").setExecutor(new AdvancedLobbyCommand());
        AdvancedLobby.getInstance().getCommand("build").setExecutor(new BuildCommand());
        AdvancedLobby.getInstance().getCommand("chatclear").setExecutor(new ChatClearCommand());
        AdvancedLobby.getInstance().getCommand("fly").setExecutor(new FlyCommand());
        AdvancedLobby.getInstance().getCommand("gamemode").setExecutor(new GameModeCommand());
        AdvancedLobby.getInstance().getCommand("globalmute").setExecutor(new GlobalMuteCommand());
        if (multiWorld_mode) {
            AdvancedLobby.getInstance().getCommand("lobby").setExecutor(new LobbyCommand());
        }
        AdvancedLobby.getInstance().getCommand("teleportall").setExecutor(new TeleportAllCommand());
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new HangingBreakByEntityListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new InventoryClickEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new LeavesDecayEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerArmorStandManipulateEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerBucketEmptyEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerBucketFillEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerChangedWorldEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerFishEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEntityEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerItemConsumeEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerItemDamageEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerMoveEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerPickupItemEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerSwapHandItemsEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerUnleashEntityEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new ServerListPingEventListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new SignChangeEventListener(), AdvancedLobby.getInstance());
    }

    public void createFiles() {
        if (!AdvancedLobby.fileLocations.exists() | !AdvancedLobby.fileMessages.exists() | !AdvancedLobby.fileSounds.exists()) {
            AdvancedLobby.getInstance().getLogger().info("One or more files were not found. Creating..");
            if (!AdvancedLobby.fileLocations.exists()) {
                AdvancedLobby.fileLocations.getParentFile().mkdirs();
                try {
                    fileLocations.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!AdvancedLobby.fileMessages.exists()) {
                AdvancedLobby.fileMessages.getParentFile().mkdirs();
                AdvancedLobby.getInstance().saveResource("messages.yml", false);
            }
            if (!AdvancedLobby.fileSounds.exists()) {
                AdvancedLobby.fileMessages.getParentFile().mkdirs();
                AdvancedLobby.getInstance().saveResource("sounds.yml", false);
            }
        }
    }

    public void loadFiles() {
        try {
            AdvancedLobby.getInstance().getLogger().info("Loading files..");
            AdvancedLobby.cfg.load(AdvancedLobby.file);
            AdvancedLobby.cfgL.load(AdvancedLobby.fileLocations);
            AdvancedLobby.cfgM.load(AdvancedLobby.fileMessages);
            AdvancedLobby.cfgS.load(AdvancedLobby.fileSounds);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile(File file, FileConfiguration cfg) {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String GO_BACK_SKULL_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6L"
            + "y90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzY"
            + "jJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";

    public static void playSound(Player player, Location location, String path) {
        try {
            if (AdvancedLobby.cfgS.getBoolean(path + ".enabled")) {
                player.playSound(location,
                        Sound.valueOf(AdvancedLobby.cfgS.getString(path + ".sound")),
                        AdvancedLobby.cfgS.getInt(path + ".volume"),
                        AdvancedLobby.cfgS.getInt(path + ".pitch"));
            }
        } catch (Exception ex) {
            AdvancedLobby.errors.put(path, ErrorType.SOUND);
        }
    }

    public static Material getMaterial(String materialString) {
        try {
            Material material = Material.getMaterial(AdvancedLobby.cfg.getString(materialString));
            if (material == null) {
                AdvancedLobby.errors.put(materialString, ErrorType.MATERIAL);
                return Material.AIR;
            }
            return material;
        } catch (Exception ex) {
            AdvancedLobby.errors.put(materialString, ErrorType.MATERIAL);
            return Material.AIR;
        }
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static boolean isOneEightVersion() {
        return Integer.parseInt(getVersion().split("_")[1]) == 8;
    }

    public static String getString(String path) {
        return ChatColor.translateAlternateColorCodes('&', AdvancedLobby.cfg.getString(path));
    }

    public static String getPlaceholderString(Player player, String path) {
        return PlaceholderAPI.setPlaceholders(player, AdvancedLobby.cfg.getString(path).replace("&", "§"));
    }

    public static int getInt(String path) {
        return AdvancedLobby.cfg.getInt(path);
    }

    public static String getName(Player player) {
        return AdvancedLobby.cfg.getBoolean("use_displaynames") ? player.getDisplayName() : player.getName();
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + getInstance().getDescription().getName() + "] " + message);
    }

    public enum ErrorType {
        SOUND, MATERIAL
    }

    public static AdvancedLobby getInstance() {
        return instance;
    }

}