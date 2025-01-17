package gg.ninjagaming.advancedlobby.misc

import gg.ninjagaming.advancedlobby.AdvancedLobby
import com.google.gson.JsonParser
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Updater() {
    private var latestVersion: String? = null
    private val currentVersion = AdvancedLobby.instance!!.pluginMeta.version
    private var updateResult: UpdateResult? = null

    enum class UpdateResult {
        UPDATE_AVAILABLE, NO_UPDATE, CONNECTION_ERROR
    }

    private fun checkLatestVersion() {
        try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/Luca-Taschner/AdvancedLobby/releases/latest"))
                .header("Accept", "application/vnd.github.v3+json")
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() != 200){
                this.setUpdateResult(UpdateResult.CONNECTION_ERROR)
                return
            }

            val body = response.body()

            val jsonObject = JsonParser.parseString(body).asJsonObject
            val latestTag = jsonObject.get("tag_name").asString
            this.latestVersion = latestTag

        } catch (e: IOException) {
            this.setUpdateResult(UpdateResult.CONNECTION_ERROR)
        }
    }

    private fun compareVersions() {
        val currentVersionCompact = currentVersion.replace(".", "").toLong()
        val latestVersionCompact = latestVersion!!.replace(".", "").replace("v","").toLong()

        if (currentVersionCompact == latestVersionCompact) {
            this.setUpdateResult(UpdateResult.NO_UPDATE)
            return
        }
        this.setUpdateResult(UpdateResult.UPDATE_AVAILABLE)
    }

    fun run() {
        AdvancedLobby.instance!!.logger.info("Searching for an update on 'Github API'..")

        this.checkLatestVersion()
        this.compareVersions()

        when (this.updateResult) {
            UpdateResult.UPDATE_AVAILABLE -> {
                AdvancedLobby.instance!!.logger.info("There was a new version found. It is recommended to update. (Visit the Github Page for more Information)")
                AdvancedLobby.updateAvailable = true
            }

            UpdateResult.NO_UPDATE -> {
                AdvancedLobby.instance!!.logger.info("The plugin is up to date.")
                AdvancedLobby.updateAvailable = false
            }

            UpdateResult.CONNECTION_ERROR -> {
                AdvancedLobby.instance!!.logger.warning("Could not connect to Github API. Retrying soon.")
                AdvancedLobby.updateAvailable = false
            }

            else -> {
                AdvancedLobby.instance!!.logger.warning("Could not connect to Github API. Retrying soon.")
                AdvancedLobby.updateAvailable = false
            }
        }
    }

    private fun setUpdateResult(updateResult: UpdateResult) {
        this.updateResult = updateResult
    }
}