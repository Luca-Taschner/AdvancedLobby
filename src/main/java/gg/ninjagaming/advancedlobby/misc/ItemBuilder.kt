package gg.ninjagaming.advancedlobby.misc

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemBuilder @JvmOverloads constructor(material: Material, amount: Int = 1) :
    ItemStack(material) {
    private val meta: ItemMeta

    init {
        this.amount = amount
        this.meta = this.itemMeta ?: throw IllegalArgumentException("ItemMeta cannot be null")
    }

    constructor(itemStack: ItemStack) : this(
        itemStack.type, itemStack.amount)

    fun setDisplayName(displayName: String?): ItemBuilder {
        if (displayName != null) {
            meta.displayName(Component.text(displayName))
        } else {
            meta.displayName(null)
        }
        return this.build()
    }

    fun setLobbyItemLore(vararg lore: String?): ItemBuilder {
        val legacySerializer: LegacyComponentSerializer = LegacyComponentSerializer.legacySection()
        val components: List<Component> = lore.mapNotNull { it?.let { line -> legacySerializer.deserialize(line) } }
        meta.lore(components)
        return this
    }

    fun addLobbyItemFlags(vararg flag: ItemFlag): ItemBuilder {
        meta.addItemFlags(*flag)
        return this.build()
    }

    fun setLobbyItemLore(lore: MutableList<String?>): ItemBuilder {
        val components = lore.map {
            it?.let { text -> LegacyComponentSerializer.legacyAmpersand().deserialize(text) }
        }.toList()

        meta.lore(components)
        return this.build()
    }

    fun build(): ItemBuilder {
        this.setItemMeta(this.meta)
        return this
    }
}
