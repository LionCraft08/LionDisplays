package dev.lionk.liondisplays

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader


class Liondisplays : ModInitializer {
    companion object{
        const val modID = "liondisplays"
        private lateinit var ModVersion: Runtime.Version
        fun getVersion(): Runtime.Version = ModVersion
    }


    override fun onInitialize() {

        // 1. Get the Optional<ModContainer> for your mod using its ID
        val modContainer = FabricLoader.getInstance().getModContainer(modID)

        // 2. Safely extract the version from the mod container
        if (modContainer.isPresent) {
            val modVersion = modContainer.get().metadata.version.friendlyString

            println("Loading $modID, Version: $modVersion")

            ModVersion = Runtime.Version.parse(modVersion)

        } else {
            // This should generally not happen in a running mod, but is good for robustness
            System.err.println("Failed to find mod container for $modID!")
        }

    }
}
