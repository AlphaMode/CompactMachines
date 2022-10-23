package dev.compactmods.machines.config;

import com.google.gson.JsonObject;
import dev.compactmods.machines.api.core.Constants;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;

public class EnableVanillaRecipesConfigCondition implements ConditionJsonProvider {

    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "config_enable_vanilla_recipes");

    @Override
    public ResourceLocation getConditionId() {
        return ID;
    }

    @Override
    public void writeParameters(JsonObject object) {
    }

    public static void register() {
        ResourceConditions.register(ID, jsonObject -> CommonConfig.ENABLE_VANILLA_RECIPES.get());
    }
}
