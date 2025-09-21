package dev.lionk.liondisplays.client.configuration

import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


object ModConfig {
    // --- Configuration Values ---
    // Define your settings with default values.
    var enabled: Boolean = true
    var enabledServer: Boolean = true
    var enabledClient: Boolean = true
    var compassDistance: Boolean = true
    var compassColoring: Boolean = true
    var heightIndicator: Boolean = true
    var offset: Int = 9

    // --- File Handling ---
    private val CONFIG_PATH: Path = FabricLoader.getInstance().getConfigDir().resolve("liondisplays.properties")
    private val properties = Properties()

    // --- Load and Save Logic ---
    /**
     * Loads the configuration from the properties file.
     * If the file doesn't exist, it creates one with default values.
     */
    fun load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                properties.load(Files.newInputStream(CONFIG_PATH))
            } catch (e: IOException) {
                // Handle error
                System.err.println("Could not read config file: $e")
            }
        }

        // Read values, using defaults if they are not present
        enabled = properties.getProperty("enabled", "true").toBoolean()
        enabledServer = properties.getProperty("enabledServer", "true").toBoolean()
        enabledClient = properties.getProperty("enabledClient", "true").toBoolean()
        compassColoring = properties.getProperty("compassColoring", "true").toBoolean()
        compassDistance = properties.getProperty("compassDistance", "true").toBoolean()
        heightIndicator = properties.getProperty("heightIndicator", "true").toBoolean()
        offset = properties.getProperty("offset", "9").toInt()

        // After loading, save to ensure any new default properties are written to the file
        save()
    }

    /**
     * Saves the current configuration values to the properties file.
     */
    fun save() {
        properties.setProperty("enabled", enabled.toString())
        properties.setProperty("enabledServer", enabledServer.toString())
        properties.setProperty("enabledClient", enabledClient.toString())
        properties.setProperty("offset", offset.toString())
        properties.setProperty("compassDistance", compassDistance.toString())
        properties.setProperty("compassColoring", compassColoring.toString())
        properties.setProperty("heightIndicator", heightIndicator.toString())


        try {
            properties.store(Files.newOutputStream(CONFIG_PATH), "lionDisplays Configuration")
        } catch (e: IOException) {
            // Handle error
            System.err.println("Could not write config file: " + e)
        }
    }
}