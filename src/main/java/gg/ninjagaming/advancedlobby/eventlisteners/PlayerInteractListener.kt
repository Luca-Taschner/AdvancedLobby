package gg.ninjagaming.advancedlobby.eventlisteners

import de.cyne.advancedlobby.AdvancedLobby
import de.cyne.advancedlobby.cosmetics.Cosmetics
import de.cyne.advancedlobby.crossversion.VParticle
import de.cyne.advancedlobby.itembuilder.ItemBuilder
import de.cyne.advancedlobby.locale.Locale
import de.cyne.advancedlobby.misc.HiderType
import gg.ninjagaming.advancedlobby.inventorybuilder.CompassInventory
import gg.ninjagaming.advancedlobby.inventorybuilder.CosmeticsInventory
import gg.ninjagaming.advancedlobby.itembuilders.PlayerHiderItemBuilder
import gg.ninjagaming.advancedlobby.misc.CooldownManager
import gg.ninjagaming.advancedlobby.misc.CooldownType
import gg.ninjagaming.advancedlobby.misc.SilentLobby
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
class PlayerInteractListener: Listener {



    private val itemStackShieldActivate = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.shield.activated.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.shield.activated.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.activated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.activated.lore"))

    private val itemStackShieldDeactivate = ItemBuilder(
        AdvancedLobby.getMaterial("hotbar_items.shield.deactivated.material"), 1,
        AdvancedLobby.cfg.getInt("hotbar_items.shield.deactivated.subid").toShort())
        .setDisplayName(AdvancedLobby.getString("hotbar_items.shield.deactivated.displayname"))
        .setLobbyItemLore(AdvancedLobby.cfg.getStringList("hotbar_items.shield.deactivated.lore"))


    private val blockedMaterials = arrayOf(
        Material.CHEST,
        Material.ENDER_CHEST,
        Material.TRAPPED_CHEST,
        Material.CRAFTING_TABLE,
        Material.FURNACE,
        Material.ENCHANTING_TABLE,
        Material.ANVIL,
        Material.BLUE_BED,
        Material.BROWN_BED,
        Material.CYAN_BED,
        Material.GRAY_BED,
        Material.GREEN_BED,
        Material.LIGHT_BLUE_BED,
        Material.LIGHT_GRAY_BED,
        Material.LIME_BED,
        Material.MAGENTA_BED,
        Material.ORANGE_BED,
        Material.PINK_BED,
        Material.PURPLE_BED,
        Material.RED_BED,
        Material.WHITE_BED,
        Material.YELLOW_BED,
        Material.JUKEBOX,
        Material.BEACON,
        Material.DISPENSER,
        Material.LEVER,
        Material.STONE_BUTTON,
        Material.ACACIA_BUTTON,
        Material.BIRCH_BUTTON,
        Material.DARK_OAK_BUTTON,
        Material.JUNGLE_BUTTON,
        Material.OAK_BUTTON,
        Material.SPRUCE_BUTTON,
        Material.DAYLIGHT_DETECTOR,
        Material.HOPPER,
        Material.DROPPER,
        Material.BREWING_STAND,
        Material.COMPARATOR,
        Material.REPEATER,
        Material.DRAGON_EGG,
        Material.NOTE_BLOCK,
        Material.CAKE,
        Material.FLOWER_POT)

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if(AdvancedLobby.multiWorld_mode && !AdvancedLobby.lobbyWorlds.contains(player.world))
            return

        if (event.action == Action.PHYSICAL)
            physicalAction(player, event)

        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK)
            clickBlockAction(player, event)


        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)
            rightClickAction(player, event)
    }

    private fun physicalAction(player: Player, event: PlayerInteractEvent) {
        if (!AdvancedLobby.build.contains(player))
            event.isCancelled = true
    }

    private fun clickBlockAction(player: Player, event: PlayerInteractEvent) {
        if (!blockedMaterials.contains(event.clickedBlock?.type))
            return

        if(AdvancedLobby.cfg.getBoolean("disable_player_interaction") && !AdvancedLobby.build.contains(player))
            event.isCancelled = true


    }

    private fun rightClickAction(player: Player, event: PlayerInteractEvent) {
        val itemStack = player.inventory.itemInMainHand

        when(itemStack.type){
            AdvancedLobby.getMaterial("hotbar_items.teleporter.material") -> compassAction(player, event, itemStack)
            AdvancedLobby.getMaterial("hotbar_items.cosmetics.material") -> cosmeticsAction(player, event, itemStack)
            Material.FEATHER -> gadgetsAction(player, event, itemStack)
            AdvancedLobby.getMaterial("hotbar_items.player_hider.show_all.material") -> hiderAction(player, event, itemStack)
            Material.getMaterial(AdvancedLobby.cfg.getString("hotbar_items.player_hider.show_vip.material")!!) -> hiderAction(player, event, itemStack)
            Material.getMaterial(AdvancedLobby.cfg.getString("hotbar_items.player_hider.show_none.material")!!) -> hiderAction(player, event, itemStack)
            AdvancedLobby.getMaterial("hotbar_items.silentlobby.activated.material") -> silentLobbyAction(player, event, itemStack)
            AdvancedLobby.getMaterial("hotbar_items.silentlobby.deactivated.material") -> silentLobbyAction(player, event, itemStack)
            AdvancedLobby.getMaterial("hotbar_items.shield.activated.material") -> shieldAction(player, event, itemStack)
            AdvancedLobby.getMaterial("hotbar_items.shield.deactivated.material") -> shieldAction(player, event ,itemStack)
            AdvancedLobby.getMaterial("hotbar_items.custom_item.material") -> customItemAction(player, event, itemStack)
            else -> return

        }

    }

    private fun compassAction(player: Player, event: PlayerInteractEvent, item: ItemStack) {

        val itemDisplayName= item.itemMeta.displayName() ?: return

        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)

        if (legacyItemName != AdvancedLobby.getString("hotbar_items.teleporter.displayname"))
            return

        event.isCancelled = true

        AdvancedLobby.playSound(player, player.location,"teleporter.open_inventory")
        CompassInventory.openInventory(player)
    }

    private fun cosmeticsAction(player: Player, event: PlayerInteractEvent, item:ItemStack) {
        val itemDisplayName= item.itemMeta.displayName() ?: return

        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)

        if (legacyItemName != AdvancedLobby.getString("hotbar_items.cosmetics.displayname"))
            return

        event.isCancelled = true
        AdvancedLobby.playSound(player, player.location, "cosmetics.open_inventory")
        CosmeticsInventory.openInventory(player)
    }

    private fun gadgetsAction(player: Player, event: PlayerInteractEvent, item: ItemStack) {
        val itemDisplayName= item.itemMeta.displayName() ?: return

        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)


        val itemName =  AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.rocket_jump_gadget.displayname"))
        if (legacyItemName != itemName)
            return

        if (!AdvancedLobby.cfg.getBoolean("hotbar_items.gadget.enabled"))
            return

        event.isCancelled = true


        if (!CooldownManager.tryCooldown(player.uniqueId, CooldownType.GADGET, 5))
        {
            AdvancedLobby.playSound(player, player.location, "gadgets.reload_gadget")
            return
        }


        AdvancedLobby.playSound(player, player.location, "gadgets.rocket_jump")
        VParticle.spawnParticle(player, "EXPLOSION_LARGE", player.location, 1)

        val players = Bukkit.getOnlinePlayers()
        players.forEach{
            if (AdvancedLobby.silentLobby.contains(it) || AdvancedLobby.silentLobby.contains(player) || AdvancedLobby.playerHider.containsKey(it))
                return

            AdvancedLobby.playSound(it, player.location, "gadgets.rocket_jump")
            VParticle.spawnParticle(it, "EXPLOSION_LARGE", player.location, 1)
        }

        Cosmetics.reloadGadget(player)
        player.velocity = Vector(0.0, 1.0, 0.0)
    }

    private fun hiderAction(player: Player, event: PlayerInteractEvent, item: ItemStack) {
        if (!item.hasItemMeta())
            return

        val itemDisplayName= item.itemMeta.displayName() ?: return

        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)

        if (legacyItemName != AdvancedLobby.getString("hotbar_items.player_hider.show_vip.displayname") && legacyItemName != AdvancedLobby.getString("hotbar_items.player_hider.show_none.displayname") && legacyItemName != AdvancedLobby.getString("hotbar_items.player_hider.show_all.displayname"))
            return


        if (!AdvancedLobby.cfg.getBoolean("hotbar_items.player_hider.enabled"))
            return

        event.isCancelled = true

        if (!CooldownManager.tryCooldown(player.uniqueId, CooldownType.HIDER, 1))
        {
            AdvancedLobby.playSound(player, player.location, "gadgets.reload_gadget")
            return
        }


        if (AdvancedLobby.silentLobby.contains(player)){
            player.sendMessage(Locale.SILENTLOBBY_FUNCTION_BLOCKED.getMessage(player))
            return
        }


        AdvancedLobby.playSound(player, player.location, "player_hider")
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20, 0))
        if (AdvancedLobby.playerHider.containsKey(player)){
            when(AdvancedLobby.playerHider[player]){
                HiderType.NONE-> playerHiderNoneAction(player)
                HiderType.VIP-> playerHiderVIPAction(player)
                else -> return
            }
            return
        }
        playerHiderAllAction(player)


    }

    private fun playerHiderNoneAction(player: Player){
        AdvancedLobby.playerHider.remove(player)

        player.sendMessage(Locale.HIDER_SHOW_ALL.getMessage(player))
        player.inventory.setItemInMainHand(PlayerHiderItemBuilder.itemStackShowAll)

        val players = Bukkit.getOnlinePlayers()
        players.forEach{
            if (it == player)
                return

            it.showPlayer(AdvancedLobby.getInstance(), player)
        }


    }

    private fun playerHiderVIPAction(player: Player){
        AdvancedLobby.playerHider[player] = HiderType.NONE

        player.sendMessage(Locale.HIDER_SHOW_NONE.getMessage(player))
        player.inventory.setItemInMainHand(PlayerHiderItemBuilder.itemStackShowNone)

        val players = Bukkit.getOnlinePlayers()
        players.forEach{
            if (it == player)
                return

            it.hidePlayer(AdvancedLobby.getInstance(), player)
        }
    }

    private fun playerHiderAllAction(player: Player){
        AdvancedLobby.playerHider[player] = HiderType.VIP

        player.sendMessage(Locale.HIDER_SHOW_VIP.getMessage(player))
        player.inventory.setItemInMainHand(PlayerHiderItemBuilder.itemStackShowVIP)

        val players = Bukkit.getOnlinePlayers()
        players.forEach{
            if (it == player)
                return

            it.hidePlayer(AdvancedLobby.getInstance(), player)
        }
    }
    private fun silentLobbyAction(player: Player, event: PlayerInteractEvent, item: ItemStack) {
        if (!item.hasItemMeta())
            return

        val itemDisplayName= item.itemMeta.displayName() ?: return

        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)

        if (legacyItemName != AdvancedLobby.getString("hotbar_items.silentlobby.activated.displayname") && legacyItemName != AdvancedLobby.getString("hotbar_items.silentlobby.deactivated.displayname"))
            return

        if (!AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.enabled"))
            return

        if (!AdvancedLobby.cfg.getBoolean("hotbar_items.silentlobby.inHotbar"))
            return

        event.isCancelled = true

        if (!CooldownManager.tryCooldown(player.uniqueId, CooldownType.SILENT_LOBBY, 1)) {
            AdvancedLobby.playSound(player, player.location, "gadgets.reload_gadget")

            return
        }

        if (AdvancedLobby.silentLobby.contains(player)){
            SilentLobby.removePlayer(player)
            return
        }

        SilentLobby.addPlayer(player)
    }


    private fun shieldAction(player: Player, event: PlayerInteractEvent, item: ItemStack) {
        if (!item.hasItemMeta())
            return

        val itemDisplayName= item.itemMeta.displayName() ?: return

        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)

        if (legacyItemName != AdvancedLobby.getString("hotbar_items.shield.activated.displayname") && legacyItemName != AdvancedLobby.getString("hotbar_items.shield.deactivated.displayname"))
            return


        if (!AdvancedLobby.cfg.getBoolean("hotbar_items.shield.enabled"))
            return

        event.isCancelled = true

        if (!CooldownManager.tryCooldown(player.uniqueId, CooldownType.SHIELD, 1)) {
            AdvancedLobby.playSound(player, player.location, "gadgets.reload_gadget")
            return
        }


        if (!AdvancedLobby.shield.contains(player)){
            shieldActivate(player)
            return
        }

        shieldDeactivate(player)
    }

    private fun shieldActivate(player: Player) {
        AdvancedLobby.shield.add(player)
        AdvancedLobby.playSound(player, player.location, "shield.enable_shield")
        player.sendMessage(Locale.SHIELD_ACTIVATE.getMessage(player))
        player.inventory.setItemInMainHand(itemStackShieldActivate)

        val entities = player.getNearbyEntities(2.5, 2.5, 2.5)
        entities.forEach{ itEntity ->
            if (itEntity !is Player)
                return

            if (itEntity.hasMetadata("NPC") || AdvancedLobby.silentLobby.contains(player) || AdvancedLobby.silentLobby.contains(itEntity))
                return

            if (itEntity.hasPermission("advancedlobby.shield.bypass"))
                return

            val nearbyPlayerVector: Vector = itEntity.location.toVector()
            val playerVector: Vector = player.location.toVector()
            val velocity = nearbyPlayerVector.clone().subtract(playerVector).normalize().multiply(0.5).setY(0.25)
            itEntity.velocity = velocity

            player.playEffect(player.location, Effect.ENDER_SIGNAL, "")

            val players = Bukkit.getOnlinePlayers()
            players.forEach{ itPlayer ->
                if (itPlayer == player)
                    return

                if (AdvancedLobby.silentLobby.contains(player) || AdvancedLobby.silentLobby.contains(itPlayer))
                    return

                if (itEntity.hasPermission("advancedlobby.shield.bypass"))
                    return

                itPlayer.playEffect(player.location, Effect.ENDER_SIGNAL, "")
            }

        }
    }

    private fun shieldDeactivate(player: Player) {
        AdvancedLobby.shield.remove(player)
        AdvancedLobby.playSound(player, player.location, "shield.disable_shield")
        player.sendMessage(Locale.SHIELD_DEACTIVATE.getMessage(player))
        player.inventory.setItemInMainHand(itemStackShieldDeactivate)
    }

    private fun customItemAction(player: Player, event: PlayerInteractEvent, item: ItemStack) {
        if (!item.hasItemMeta())
            return

        val itemDisplayName= item.itemMeta.displayName() ?: return

        val legacyItemName = LegacyComponentSerializer.legacySection().serialize(itemDisplayName)

        if (legacyItemName != AdvancedLobby.getString("hotbar_items.custom_item.displayname"))
            return

        event.isCancelled = true

        if (AdvancedLobby.cfg.getString("hotbar_items.custom_item.command") == null)
            return

        val commandString = AdvancedLobby.cfg.getString("hotbar_items.custom_item.command")?: return
        player.performCommand(commandString)

    }

}