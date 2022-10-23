package dev.compactmods.machines.api.core;

import net.minecraft.resources.ResourceLocation;

public abstract class Constants {
    public static final String MOD_ID = "compactmachines";

    public static final ResourceLocation STORAGE_TYPE_REGISTRY = new ResourceLocation(MOD_ID, "storage_types");

    public static final ResourceLocation ITEM_TUNNEL_KEY = new ResourceLocation(MOD_ID, "item_tunnel");
    public static final ResourceLocation FLUID_TUNNEL_KEY = new ResourceLocation(MOD_ID, "fluid_tunnel");
    public static final ResourceLocation ENERGY_TUNNEL_KEY = new ResourceLocation(MOD_ID, "energy_tunnel");
}
