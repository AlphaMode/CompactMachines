package dev.compactmods.machines.api.room.upgrade;

import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.api.room.registration.IRoomRegistration;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public interface RoomUpgrade {

    ResourceKey<Registry<RoomUpgrade>> REG_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Constants.MOD_ID, "room_upgrade"));

    String UNNAMED_TRANS_KEY = "item." + Constants.MOD_ID + ".upgrades.unnamed";

    default String getTranslationKey() {
        return UNNAMED_TRANS_KEY;
    }

    default String getTranslationKey(ItemStack stack) {
        return getTranslationKey();
    }

    /**
     * Called when an upgrade is first applied to a room.
     */
    default void onAdded(ServerLevel level, IRoomRegistration room) {}

    /**
     * Called when an update is removed from a room.
     */
    default void onRemoved(ServerLevel level, IRoomRegistration room) {}
}
