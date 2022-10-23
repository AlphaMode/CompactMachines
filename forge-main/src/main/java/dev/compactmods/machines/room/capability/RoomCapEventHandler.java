package dev.compactmods.machines.room.capability;

import dev.compactmods.machines.room.RoomCapabilities;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;

public class RoomCapEventHandler implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(RoomCapabilities.ROOM_HISTORY, PlayerRoomHistoryProvider::new); // new ResourceLocation(Constants.MOD_ID, "room_history")

        registry.registerForPlayers(PlayerRoomMetadataProviderProvider.CURRENT_ROOM_META, PlayerRoomMetadataProviderProvider::new); // new ResourceLocation(Constants.MOD_ID, "room_metadata")
    }
}
