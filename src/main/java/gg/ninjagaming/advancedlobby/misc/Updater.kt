package gg.ninjagaming.advancedlobby.misc

import gg.ninjagaming.advancedlobby.AdvancedLobby
import com.google.gson.JsonParser
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class Updater(private val currentVersion: String) {

    enum class UpdateResult {
        UPDATE_AVAILABLE, NO_UPDATE, CONNECTION_ERROR, CONNECTION_TIMEOUT
    }

    private fun checkLatestVersion(): UpdateResult {
        try {
            val client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()


            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/Luca-Taschner/AdvancedLobby/releases/latest"))
                .header("Accept", "application/vnd.github.v3+json")
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build()

            val responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())


            val response = responseFuture.join()

            if (response.statusCode() != 200){
                return UpdateResult.CONNECTION_ERROR
            }

            val body = response.body()

            val jsonObject = JsonParser.parseString(body).asJsonObject
            val latestTag = jsonObject.get("tag_name").asString

            return compareVersions(latestTag)

        } catch (_: IOException) {
            return UpdateResult.CONNECTION_ERROR
        }catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            return UpdateResult.CONNECTION_TIMEOUT
        }catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            return UpdateResult.CONNECTION_TIMEOUT
        }

    }

    private fun compareVersions(latestVersion: String): UpdateResult {
        val currentVersionCompact = currentVersion.replace(".", "").toLong()
        val latestVersionCompact = latestVersion.replace(".", "").replace("v","").toLong()

        if (currentVersionCompact == latestVersionCompact)
            return UpdateResult.NO_UPDATE

        return UpdateResult.UPDATE_AVAILABLE
    }

    fun run() {
        AdvancedLobby.instance!!.logger.info("Searching for an update on 'Github API'..")

        val updateResult = checkLatestVersion()

        when (updateResult) {
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

            UpdateResult.CONNECTION_TIMEOUT -> {
                AdvancedLobby.instance!!.logger.warning("Request to Github API Timed out. Retrying soon.")
                AdvancedLobby.updateAvailable = false
            }
        }
    }
}