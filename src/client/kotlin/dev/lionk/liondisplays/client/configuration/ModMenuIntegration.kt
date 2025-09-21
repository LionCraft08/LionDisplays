package dev.lionk.liondisplays.client.configuration

// Import your config class
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.lionk.liondisplays.client.messaging.DisplayData
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.minecraft.text.Text
import java.lang.String

class ModMenuIntegration : ModMenuApi {

    override fun getModConfigScreenFactory() = ConfigScreenFactory { parent ->
        // Load the config on screen open, in case it was changed elsewhere.
        ModConfig.load()

        // Get a ConfigBuilder instance.
        val builder: ConfigBuilder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("title.liondisplays.config"))

        // Set a save consumer. This is called when the user clicks "Save".
        builder.setSavingRunnable({
            // The values are already updated in the config object, so we just need to save.
            ModConfig.save()
        })

        // Get a ConfigEntryBuilder instance to create option entries.
        val entryBuilder: ConfigEntryBuilder = builder.entryBuilder()

        // --- Create a Category ---
        val general: ConfigCategory = builder.getOrCreateCategory(Text.translatable("category.liondisplays.general"))

        // --- Add Entries to the Category ---

        // Boolean Toggle for 'enableAwesomeFeature'
        general.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("option.liondisplays.enabled"),
                ModConfig.enabled
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Text.translatable("option.liondisplays.enabled.tooltip")) // Optional tooltip
                .setSaveConsumer({ newValue ->
                    ModConfig.enabled = newValue
                    if (newValue == false){
                        DisplayData.values.clear()
                    }
                }) // Update config value on change
                .build()
        )
        general.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("option.liondisplays.server.enabled"),
                ModConfig.enabledServer
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Text.translatable("option.liondisplays.server.enabled.tooltip")) // Optional tooltip
                .setSaveConsumer({ newValue ->
                    ModConfig.enabledServer = newValue
                }) // Update config value on change
                .build()
        )

        general.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("option.liondisplays.client.enabled"),
                ModConfig.enabledClient
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Text.translatable("option.liondisplays.client.enabled.tooltip")) // Optional tooltip
                .setSaveConsumer({ newValue ->
                    ModConfig.enabledClient = newValue
                }) // Update config value on change
                .build()
        )

        general.addEntry(
            entryBuilder.startIntSlider(
                Text.translatable("option.liondisplays.offset"),
                ModConfig.offset,
                0,
                50
            )
                .setDefaultValue(9)
                .setTooltip(Text.translatable("option.liondisplays.offset.tooltip"))
                .setSaveConsumer { newValue -> ModConfig.offset = newValue }
                .setTextGetter { value -> Text.literal(String.valueOf(value)) } // Text to display next to the slider
                .build()
        )

        val compass: ConfigCategory = builder.getOrCreateCategory(Text.translatable("category.liondisplays.compass"))

        compass.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("option.liondisplays.compass.distance"),
                ModConfig.compassDistance
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Text.translatable("option.liondisplays.compass.distance.tooltip")) // Optional tooltip
                .setSaveConsumer({ newValue ->
                    ModConfig.compassDistance = newValue
                }) // Update config value on change
                .build()
        )
        compass.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("option.liondisplays.compass.height"),
                ModConfig.heightIndicator
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Text.translatable("option.liondisplays.compass.height.tooltip"))
                .setSaveConsumer({ newValue ->
                    ModConfig.heightIndicator = newValue
                }) // Update config value on change
                .build()
        )
        compass.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("option.liondisplays.compass.color"),
                ModConfig.compassColoring
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Text.translatable("option.liondisplays.compass.color.tooltip"))
                .setSaveConsumer({ newValue ->
                    ModConfig.compassColoring = newValue
                }) // Update config value on change
                .build()
        )

        builder.build()
    }
}