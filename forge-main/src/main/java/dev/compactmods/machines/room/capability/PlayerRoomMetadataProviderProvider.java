package dev.compactmods.machines.room.capability;

import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.api.room.IPlayerRoomMetadataProvider;
import dev.compactmods.machines.room.PlayerRoomMetadataProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

// Provider provider. Because Java.
public class PlayerRoomMetadataProviderProvider implements PlayerComponent<PlayerRoomMetadataProviderProvider> {
    public static final ComponentKey<PlayerRoomMetadataProviderProvider> CURRENT_ROOM_META = ComponentRegistry.getOrCreate(new ResourceLocation(Constants.MOD_ID, "room_metadata"), PlayerRoomMetadataProviderProvider.class);

    private final IPlayerRoomMetadataProvider provider;

    public PlayerRoomMetadataProviderProvider(Player player) {
        provider = new PlayerRoomMetadataProvider();
    }

    public IPlayerRoomMetadataProvider getCurrentRoomMetadataProvider() {
        return this.provider;
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        provider.currentRoom().ifPresent(meta -> {
            tag.putString("room", meta.roomCode());
            tag.putUUID("owner", meta.owner());
        });
    }

    @Override
    public void readFromNbt(CompoundTag nbt) {
        if(nbt.isEmpty()) return;
        provider.setCurrent(new PlayerRoomMetadataProvider.CurrentRoomData(nbt.getString("room"), nbt.getUUID("owner")));
    }
}
