package gg.ninjagaming.advancedlobby

import gg.ninjagaming.advancedlobby.commands.*
import gg.ninjagaming.advancedlobby.commands.advancedlobby.AdvancedLobbyCommand
import gg.ninjagaming.advancedlobby.eventlisteners.block.BlockBreakEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.block.BlockPlaceEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.block.LeavesDecayEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.block.SignChangeEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.entity.EntityDamageEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.entity.EntityExplodeEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.entity.FoodLevelChangeEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.hanging.HangingBreakByEntityEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.inventory.InventoryClickEventListener
import gg.ninjagaming.advancedlobby.eventlisteners.player.*
import gg.ninjagaming.advancedlobby.eventlisteners.server.ServerListPingEventListener
import gg.ninjagaming.advancedlobby.misc.HiderType
import gg.ninjagaming.advancedlobby.misc.Updater
import gg.ninjagaming.advancedlobby.misc.extras.cosmetics.Cosmetics
import gg.ninjagaming.advancedlobby.misc.extras.cosmetics.Cosmetics.startBalloonTask
import gg.ninjagaming.advancedlobby.runnables.ActionBarRunnable.schedulingRunnable
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.*
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.io.IOException
import java.util.*

class AdvancedLobby : JavaPlugin() {
    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        this.createFiles()
        this.loadFiles()

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            instance!!.log("PlaceholderAPI was found. Connected.")
            placeholderApi = true
        }

        updater = Updater(35799)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance!!, {
            updater!!.run()
        }, 0L, (20 * 60 * 60 * 24).toLong()) //once a day

        if (cfg.getBoolean("actionbar.enabled")) {
            actionbarMessages.addAll(cfg.getStringList("actionbar.messages"))
            prepareActionBarRunnable()
        }

        multiWorld_mode = cfg.getBoolean("multiworld_mode.enabled")

        for (world: World in Bukkit.getWorlds()) {
            if (cfg.getStringList("lobby_worlds").contains(world.name)) {
                lobbyWorlds.add(world)
            }
        }

        this.prepareLobbyWorlds()

        this.registerCommands()
        this.registerListener()

        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")

        startBalloonTask()

        //metrics = new Metrics(AdvancedLobby.getInstance(), 7014);
        //metrics.addCustomChart(new Metrics.SimplePie("singleworld_mode", () -> multiWorld_mode ? "enabled" : "disabled"));
    }

    override fun onDisable() {
        for (players: Player? in Bukkit.getOnlinePlayers()) {
            if (Cosmetics.balloons.containsKey(players)) {
                Cosmetics.balloons[players]!!.remove()
            }
        }
    }

    private fun prepareLobbyWorlds() {
        for (world: World in if (multiWorld_mode) lobbyWorlds else Bukkit.getWorlds()) {
            val weatherType: String = cfg.getString("weather.weather_type")!!.uppercase(Locale.getDefault())
            when (weatherType) {
                ("CLEAR") -> {
                    world.setStorm(false)
                    world.isThundering = false
                }

                ("RAIN") -> {
                    world.setStorm(true)
                    world.isThundering = false
                }

                ("THUNDER") -> {
                    world.setStorm(true)
                    world.isThundering = true
                }
            }

            if (!cfg.getBoolean("weather.enabled")) world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        }
    }

    private fun registerCommands() {
        instance!!.getCommand("advancedlobby")!!.setExecutor(AdvancedLobbyCommand())
        instance!!.getCommand("build")!!.setExecutor(BuildCommand())
        instance!!.getCommand("chatclear")!!.setExecutor(ChatClearCommand())
        instance!!.getCommand("fly")!!.setExecutor(FlyCommand())
        instance!!.getCommand("gamemode")!!.setExecutor(GameModeCommand())
        instance!!.getCommand("globalmute")!!.setExecutor(GlobalMuteCommand())
        if (multiWorld_mode) {
            instance!!.getCommand("lobby")!!.setExecutor(LobbyCommand())
        }
        instance!!.getCommand("teleportall")!!.setExecutor(TeleportAllCommand())
    }

    private fun registerListener() {
        Bukkit.getPluginManager().registerEvents(AsyncPlayerChatEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(BlockBreakEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(BlockPlaceEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(EntityDamageEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(EntityExplodeEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(FoodLevelChangeEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(
            HangingBreakByEntityEventListener(),
            instance!!
        )
        Bukkit.getPluginManager().registerEvents(InventoryClickEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(LeavesDecayEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(
            PlayerArmorStandManipulateEventListener(),
            instance!!
        )
        Bukkit.getPluginManager().registerEvents(PlayerBucketEmptyEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerBucketFillEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerChangedWorldEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(
            PlayerCommandPreprocessEventListener(),
            instance!!
        )
        Bukkit.getPluginManager().registerEvents(PlayerDropItemEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerFishEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(
            PlayerInteractEntityEventListener(),
            instance!!
        )
        Bukkit.getPluginManager().registerEvents(PlayerInteractEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerItemConsumeEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerItemDamageEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerItemHeldEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerJoinEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerMoveEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerPickupItemEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerQuitEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerSwapHandItemsEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerTeleportEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(PlayerUnleashEntityEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(ServerListPingEventListener(), instance!!)
        Bukkit.getPluginManager().registerEvents(SignChangeEventListener(), instance!!)
    }

    fun createFiles() {
        if (!fileLocations.exists() or !fileMessages.exists() or !fileSounds.exists()) {
            instance!!.logger.info("One or more files were not found. Creating..")
            if (!fileLocations.exists()) {
                fileLocations.parentFile.mkdirs()
                try {
                    fileLocations.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (!fileMessages.exists()) {
                fileMessages.parentFile.mkdirs()
                instance!!.saveResource("messages.yml", false)
            }
            if (!fileSounds.exists()) {
                fileMessages.parentFile.mkdirs()
                instance!!.saveResource("sounds.yml", false)
            }
        }
    }

    private fun loadFiles() {
        try {
            instance!!.logger.info("Loading files..")
            cfg.load(Companion.file)
            cfgL.load(fileLocations)
            cfgM.load(fileMessages)
            cfgS.load(fileSounds)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    fun log(message: String) {
        Bukkit.getConsoleSender().sendMessage("[" + instance!!.description.name + "] " + message)
    }

    enum class ErrorType {
        SOUND, MATERIAL
    }

    companion object {
        var instance: AdvancedLobby? = null
            private set

        var file: File = File("plugins/AdvancedLobby", "config.yml")
        var cfg: FileConfiguration = YamlConfiguration.loadConfiguration(file)

        var fileLocations: File = File("plugins/AdvancedLobby", "locations.yml")
        var cfgL: FileConfiguration = YamlConfiguration.loadConfiguration(fileLocations)

        var fileMessages: File = File("plugins/AdvancedLobby", "messages.yml")
        var cfgM: FileConfiguration = YamlConfiguration.loadConfiguration(fileMessages)

        var fileSounds: File = File("plugins/AdvancedLobby", "sounds.yml")
        var cfgS: FileConfiguration = YamlConfiguration.loadConfiguration(fileSounds)

        var actionbarMessages: ArrayList<String> = ArrayList()
        var build: ArrayList<Player> = ArrayList()
        var fly: ArrayList<Player> = ArrayList()
        var shield: ArrayList<Player> = ArrayList()
        var silentLobby: ArrayList<Player> = ArrayList()
        var buildInventory: HashMap<Player, Array<ItemStack>> = HashMap()
        var playerHider: HashMap<Player, HiderType> = HashMap()

        var globalMute: Boolean = false
        var updateAvailable: Boolean = false
        var placeholderApi: Boolean = false

        var multiWorld_mode: Boolean = false

        var lobbyWorlds: ArrayList<World> = ArrayList()
        var updater: Updater? = null

        var errors: HashMap<String, ErrorType> = HashMap()

        //public Metrics metrics;
        var actionBarRunnable: BukkitTask? = null

        fun prepareActionBarRunnable() {
            actionBarRunnable = instance!!.server.scheduler
                .runTaskTimer(
                    instance!!,
                    schedulingRunnable(actionbarMessages, cfg.getInt("actionbar.display_time")),
                    0L,
                    cfg.getInt("actionbar.display_time") * 20L * actionbarMessages.size
                )
        }

        fun playSound(player: Player, location: Location, path: String) {
            try {
                if (cfgS.getBoolean("$path.enabled")) {
                    player.playSound(
                        location,
                        Sound.valueOf(cfgS.getString("$path.sound")!!),
                        cfgS.getInt("$path.volume").toFloat(),
                        cfgS.getInt("$path.pitch").toFloat()
                    )
                }
            } catch (ex: Exception) {
                errors[path] = ErrorType.SOUND
            }
        }

        fun getMaterial(materialString: String): Material {
            try {
                val material: Material? = Material.getMaterial(cfg.getString(materialString)!!)
                if (material == null) {
                    errors[materialString] =
                        ErrorType.MATERIAL
                    return Material.AIR
                }
                return material
            } catch (ex: Exception) {
                errors[materialString] =
                    ErrorType.MATERIAL
                return Material.AIR
            }
        }

        val version: String
            get() = Bukkit.getServer().javaClass.getPackage().name.split("\\.".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()[3]


        fun getString(path: String): String {
            return cfg.getString(path)!!
        }

        fun getPlaceholderString(player: Player?, path: String): String {
            return PlaceholderAPI.setPlaceholders(player, cfg.getString(path)!!.replace("&", "§"))
        }

        fun getInt(path: String): Int {
            return cfg.getInt(path)
        }

        fun getName(player: Player): String {
            return if (cfg.getBoolean("use_displaynames")) player.displayName else player.name
        }
    }


}