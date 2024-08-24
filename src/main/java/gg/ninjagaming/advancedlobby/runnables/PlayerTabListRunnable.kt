package gg.ninjagaming.advancedlobby.runnables

import de.cyne.advancedlobby.AdvancedLobby
import gg.ninjagaming.advancedlobby.titleapi.TitleApi.sendTabTitle
import org.bukkit.entity.Player

object PlayerTabListRunnable {
    fun schedulingRunnable(targetPlayer: Player): Runnable {
        val r = Runnable {
            sendTabListToPlayer(targetPlayer)
        }
        return r
    }

    private fun sendTabListToPlayer(player: Player) {
        if (AdvancedLobby.placeholderApi) {
            sendTabTitle(
                player, AdvancedLobby.getPlaceholderString(player, "tablist_header_footer.header"),
                AdvancedLobby.getPlaceholderString(player, "tablist_header_footer.footer")
            )
            return
        }

        sendTabTitle(
            player, AdvancedLobby.getString("tablist_header_footer.header"),
            AdvancedLobby.getString("tablist_header_footer.footer")
        )

    }
}