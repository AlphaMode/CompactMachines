package dev.compactmods.machines.core;

import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.room.network.PlayerRequestedTeleportPacket;
import dev.compactmods.machines.room.network.SyncRoomMetadataPacket;
import dev.compactmods.machines.tunnel.network.TunnelAddedPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class CompactMachinesNet {

    public static final SimpleChannel CHANNEL = new SimpleChannel(
            new ResourceLocation(Constants.MOD_ID, "main")
    );


    public static void setupMessages() {
        CHANNEL.registerS2CPacket(TunnelAddedPacket.class, 1);

        CHANNEL.registerC2SPacket(PlayerRequestedTeleportPacket.class, 2);

        CHANNEL.registerS2CPacket(SyncRoomMetadataPacket.class, 3);
    }
}
