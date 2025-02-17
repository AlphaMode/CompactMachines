package dev.compactmods.machines.api.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class CMTags {

    public static final TagKey<Item> ROOM_UPGRADE_ITEM = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Constants.MOD_ID, "room_upgrade"));
    public static final TagKey<Item> MACHINE_ITEM = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Constants.MOD_ID, "machine"));
    public static final TagKey<Block> MACHINE_BLOCK = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Constants.MOD_ID, "machine"));
}
