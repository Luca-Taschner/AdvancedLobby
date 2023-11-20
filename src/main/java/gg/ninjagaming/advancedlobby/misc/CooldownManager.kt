package gg.ninjagaming.advancedlobby.misc

import com.google.common.collect.HashBasedTable
import java.util.*


object CooldownManager {
    private val cooldownTable: HashBasedTable<String,CooldownType,Long> = HashBasedTable.create()

    private fun calculateCooldownRemainder(expireTime :Long?): Long {
        return if (expireTime != null) expireTime - System.currentTimeMillis() else Long.MIN_VALUE
    }

    fun getCooldown(uuid: UUID, key: CooldownType?): Long {
        return calculateCooldownRemainder(cooldownTable.get(uuid.toString(), key))
    }

    fun setCooldown(uuid: UUID, key: CooldownType?, delay: Long): Long {
        return calculateCooldownRemainder(cooldownTable.put(uuid.toString(), key, System.currentTimeMillis() + delay * 1000))
    }

    fun tryCooldown(uuid: UUID?, key: CooldownType?, delay: Long): Boolean {
        if (getCooldown(uuid!!, key) / 1000 > 0) return false
        setCooldown(uuid, key, delay + 1)
        return true
    }
}

enum class CooldownType {
    TELEPORT,
    GADGET,
    HIDER,
    SILENT_LOBBY,
    COSMETIC,
    SHIELD
}