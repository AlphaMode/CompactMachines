package dev.compactmods.machines.room.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public record SyncRoomMetadataPacket(String roomCode, UUID owner) implements S2CPacket {
    public SyncRoomMetadataPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUUID());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(roomCode);
        buffer.writeUUID(owner);
    }

    public void handle(Minecraft minecraft, ClientPacketListener clientPacketListener, PacketSender packetSender, SimpleChannel simpleChannel) {
        ClientRoomNetworkHandler.handleRoomSync(this);
    }
}
