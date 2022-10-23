package dev.compactmods.machines.room;

import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.room.capability.PlayerRoomHistoryProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.resources.ResourceLocation;

public class RoomCapabilities {

    public static final ComponentKey<PlayerRoomHistoryProvider> ROOM_HISTORY = ComponentRegistry.getOrCreate(new ResourceLocation(Constants.MOD_ID, "room_history"), PlayerRoomHistoryProvider.class);


}