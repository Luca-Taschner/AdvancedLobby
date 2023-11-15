package gg.ninjagaming.advancedlobby.titleapi

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.Times
import net.kyori.adventure.title.Title.title

import org.bukkit.entity.Player
import java.time.Duration

object TitleApi {

    fun sendActionBar(player: Player, message :String)
    {
        player.sendActionBar { generateBaseComponent(message) }
    }

    fun sendTitle(player: Player, fadeIn: Int, stay: Int, fadeOut: Int, titleText: String, subtitleText: String)
    {
        val mainTitle = Component.text(titleText)
        val subtitle = Component.text(subtitleText)
        val times = generateTitleTimes(fadeIn, stay, fadeOut)
        player.showTitle(title(mainTitle, subtitle, times))
    }

    fun sendTabTitle(player: Player, header: String, footer: String)
    {
        player.sendPlayerListHeaderAndFooter(generateTextComponent(header), generateTextComponent(footer))
    }

    private fun generateBaseComponent(message: String): Component{
        return Component.text()
            .content(message).build()
    }

    private fun generateTitleTimes(fadeIn: Int, stay: Int, fadeOut: Int): Times{
        return Times.times(Duration.ofSeconds(fadeIn.toLong()), Duration.ofSeconds(stay.toLong()), Duration.ofSeconds(fadeOut.toLong()))
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