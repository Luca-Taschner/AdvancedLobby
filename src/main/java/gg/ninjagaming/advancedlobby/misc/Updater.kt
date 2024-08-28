package gg.ninjagaming.advancedlobby.misc

import gg.ninjagaming.advancedlobby.AdvancedLobby
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class Updater(private val resourceId: Long) {
    private var latestVersion: String? = null
    private val currentVersion = AdvancedLobby.instance!!.description.version
    private var updateResult: UpdateResult? = null

    enum class UpdateResult {
        UPDATE_AVAILABLE, NO_UPDATE, CONNECTION_ERROR
    }

    private fun checkLatestVersion() {
        try {
            val httpConnection = URL.of(URI.create("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId),null)
                .openConnection() as HttpURLConnection
            this.latestVersion = BufferedReader(InputStreamReader(httpConnection.inputStream)).readLine()
        } catch (e: IOException) {
            this.setUpdateResult(UpdateResult.CONNECTION_ERROR)
        }
    }

    private fun compareVersions() {
        val currentVersionCompact = currentVersion.replace(".", "").toLong()
        val latestVersionCompact = latestVersion!!.replace(".", "").toLong()

        if (currentVersionCompact == latestVersionCompact) {
            this.setUpdateResult(UpdateResult.NO_UPDATE)
            return
        }
        this.setUpdateResult(UpdateResult.UPDATE_AVAILABLE)
    }

    fun run() {
        AdvancedLobby.instance!!.logger.info("Searching for an update on 'spigotmc.org'..")

        this.checkLatestVersion()
        this.compareVersions()

        when (this.updateResult) {
            UpdateResult.UPDATE_AVAILABLE -> {
                AdvancedLobby.instance!!.logger.info("There was a new version found. It is recommended to update. (Visit spigotmc.org)")
                AdvancedLobby.updateAvailable = true
            }

            UpdateResult.NO_UPDATE -> {
                AdvancedLobby.instance!!.logger.info("The plugin is up to date.")
                AdvancedLobby.updateAvailable = false
            }

            UpdateResult.CONNECTION_ERROR -> {
                AdvancedLobby.instance!!.logger.warning("Could not connect to spigotmc.org. Retrying soon.")
                AdvancedLobby.updateAvailable = false
            }

            else -> {
                AdvancedLobby.instance!!.logger.warning("Could not connect to spigotmc.org. Retrying soon.")
                AdvancedLobby.updateAvailable = false
            }
        }
    }

    private fun setUpdateResult(updateResult: UpdateResult) {
        this.updateResult = updateResult
    }
}