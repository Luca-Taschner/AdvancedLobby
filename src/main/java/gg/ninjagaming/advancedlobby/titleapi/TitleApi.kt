package gg.ninjagaming.advancedlobby.titleapi

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.Times

import org.bukkit.entity.Player
import java.time.Duration

object TitleApi {

    fun sendActionBar(player: Player, message :String)
    {
        player.sendActionBar { generateTextComponent(message) }
    }

    fun sendTitle(player: Player, fadeIn: Int, stay: Int, fadeOut: Int, title: String, subtitle: String)
    {
        val times = generateTitleTimes(fadeIn, stay, fadeOut)
        player.showTitle(generateTitle(title,subtitle, times))
    }

    fun sendTabTitle(player: Player, header: String, footer: String)
    {
        player.sendPlayerListHeaderAndFooter(generateTextComponent(header), generateTextComponent(footer))
    }

    private fun generateBaseComponent(message: String): Component{
        return Component.text()
            .content(message).build()
    }

    private fun generateTitle(title: String,subtitle: String, times: Times): Title{
        return Title.title(generateBaseComponent(title), generateBaseComponent(subtitle),times)

    }

    private fun generateTitleTimes(fadeIn: Int, stay: Int, fadeOut: Int): Times{
        return Times.times(Duration.ofMillis(fadeIn.toLong()), Duration.ofMillis(stay.toLong()), Duration.ofMillis(fadeOut.toLong()))
    }

    private fun generateTextComponent(message: String): TextComponent{
        val messageStrings = message.split("""\n""")

        val component = Component.text()

        messageStrings.forEach {

            component.append(Component.text(it))
            if(it != messageStrings.last())
                component.append(Component.newline())
        }


        return component.build()

    }
}