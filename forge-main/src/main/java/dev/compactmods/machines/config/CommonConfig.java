package dev.compactmods.machines.config;

import dev.compactmods.machines.CompactMachines;
import dev.compactmods.machines.api.core.Constants;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class CommonConfig {

    public static ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.BooleanValue ENABLE_VANILLA_RECIPES;



    static {
        generateConfig();
    }

    private static void generateConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder
                .comment("Recipes and Integrations")
                .push("recipes");

        ENABLE_VANILLA_RECIPES = builder
                .comment("Enable vanilla-style recipes.")
                .define("vanillaRecipes", true);

        builder.pop();

        CONFIG = builder.build();
    }

    public static void onLoaded(ModConfig config) {
        CompactMachines.LOGGER.debug("Loading common configuration...");
    }
}
