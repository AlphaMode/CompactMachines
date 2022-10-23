package dev.compactmods.machines.room.network;

import dev.compactmods.machines.api.core.Constants;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class RoomNetworkHandler {

    public static final SimpleChannel CHANNEL = new SimpleChannel(
            new ResourceLocation(Constants.MOD_ID, "room_tracking")
    );

    public static void setupMessages() {
        CHANNEL.registerC2SPacket(PlayerStartedRoomTrackingPacket.class, 1);

        CHANNEL.registerS2CPacket(InitialRoomBlockDataPacket.class, 2);
    }
}
