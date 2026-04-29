package dev.lionk.liondisplays.client.configuration

// Import your config class
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.lionk.liondisplays.client.messaging.CompassDimensionHandling
import dev.lionk.liondisplays.client.messaging.DisplayAttachments
import dev.lionk.liondisplays.client.messaging.DisplayData
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.minecraft.network.chat.Component
import java.lang.String

class ModMenuIntegration : ModMenuApi {

    override fun getModConfigScreenFactory() = ConfigScreenFactory { parent ->
        // Load the config on screen open, in case it was changed elsewhere.
        ModConfig.load()

        // Get a ConfigBuilder instance.
        val builder: ConfigBuilder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("title.liondisplays.config"))

        // Set a save consumer. This is called when the user clicks "Save".
        builder.setSavingRunnable({
            // The values are already updated in the config object, so we just need to save.
            ModConfig.save()
        })

        // Get a ConfigEntryBuilder instance to create option entries.
        val entryBuilder: ConfigEntryBuilder = builder.entryBuilder()

        // --- Create a Category ---
        val general: ConfigCategory = builder.getOrCreateCategory(Component.translatable("category.liondisplays.general"))

        // --- Add Entries to the Category ---

        // Boolean Toggle for 'enableAwesomeFeature'
        general.addEntry(
            entryBuilder.startBooleanToggle(
                Component.translatable("option.liondisplays.enabled"),
                ModConfig.enabled
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Component.translatable("option.liondisplays.enabled.tooltip")) // Optional tooltip
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
                Component.translatable("option.liondisplays.server.enabled"),
                ModConfig.enabledServer
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Component.translatable("option.liondisplays.server.enabled.tooltip")) // Optional tooltip
                .setSaveConsumer({ newValue ->
                    ModConfig.enabledServer = newValue
                }) // Update config value on change
                .build()
        )

        general.addEntry(
            entryBuilder.startBooleanToggle(
                Component.translatable("option.liondisplays.client.enabled"),
                ModConfig.enabledClient
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Component.translatable("option.liondisplays.client.enabled.tooltip")) // Optional tooltip
                .setSaveConsumer({ newValue ->
                    ModConfig.enabledClient = newValue
                }) // Update config value on change
                .build()
        )

        general.addEntry(
            entryBuilder.startBooleanToggle(
                Component.translatable("option.liondisplays.message.enabled"),
                ModConfig.enabledMessage
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Component.translatable("option.liondisplays.message.enabled.tooltip")) // Optional tooltip
                .setSaveConsumer { newValue ->
                    ModConfig.enabledMessage = newValue
                } // Update config value on change
                .build()
        )

        general.addEntry(
            entryBuilder.startIntField(
                Component.translatable("option.liondisplays.transition-duration"),
                ModConfig.transitionBuffer
            )
                .setDefaultValue(500) // Default value for the 'reset' button
                .setTooltip(Component.translatable("option.liondisplays.transition-duration.tooltip")) // Optional tooltip
                .setSaveConsumer { newValue ->
                    ModConfig.transitionBuffer = newValue
                } // Update config value on change
                .build()
        )

        general.addEntry(
            entryBuilder.startIntSlider(
                Component.translatable("option.liondisplays.offset"),
                ModConfig.offset,
                0,
                50
            )
                .setDefaultValue(9)
                .setTooltip(Component.translatable("option.liondisplays.offset.tooltip"))
                .setSaveConsumer { newValue -> ModConfig.offset = newValue }
                .setTextGetter { value -> Component.literal(String.valueOf(value)) } // Component to display next to the slider
                .build()
        )

        general.addEntry(
            entryBuilder.startEnumSelector<DisplayAttachments>(
                Component.translatable("option.liondisplays.defaultattachment"),
                DisplayAttachments::class.java,
                ModConfig.defaultAttachment
            )
                .setDefaultValue { DisplayAttachments.TOP_LEFT }
                .setTooltip(Component.translatable("option.liondisplays.defaultattachment.tooltip"))
                .setSaveConsumer { newValue -> ModConfig.defaultAttachment = newValue }
                .build()
        )

        val compass: ConfigCategory = builder.getOrCreateCategory(Component.translatable("category.liondisplays.compass"))

        compass.addEntry(
            entryBuilder.startBooleanToggle(
                Component.translatable("option.liondisplays.compass.distance"),
                ModConfig.compassDistance
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Component.translatable("option.liondisplays.compass.distance.tooltip")) // Optional tooltip
                .setSaveConsumer({ newValue ->
                    ModConfig.compassDistance = newValue
                }) // Update config value on change
                .build()
        )
        compass.addEntry(
            entryBuilder.startBooleanToggle(
                Component.translatable("option.liondisplays.compass.height"),
                ModConfig.heightIndicator
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Component.translatable("option.liondisplays.compass.height.tooltip"))
                .setSaveConsumer({ newValue ->
                    ModConfig.heightIndicator = newValue
                }) // Update config value on change
                .build()
        )
        compass.addEntry(
            entryBuilder.startBooleanToggle(
                Component.translatable("option.liondisplays.compass.color"),
                ModConfig.compassColoring
            )
                .setDefaultValue(true) // Default value for the 'reset' button
                .setTooltip(Component.translatable("option.liondisplays.compass.color.tooltip"))
                .setSaveConsumer({ newValue ->
                    ModConfig.compassColoring = newValue
                }) // Update config value on change
                .build()
        )
        compass.addEntry(
            entryBuilder.startEnumSelector(
                Component.translatable("option.liondisplays.compass.wrong_dimension"),
                CompassDimensionHandling::class.java,
                ModConfig.dimensionManagement
            )
                .setDefaultValue { CompassDimensionHandling.ERROR }
                .setTooltip(Component.translatable("option.liondisplays.compass.wrong_dimension.tooltip"))
                .setSaveConsumer { newValue -> ModConfig.dimensionManagement = newValue }
                .build()
        )

        builder.build()
    }
}