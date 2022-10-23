package dev.compactmods.machines.room.network;

import dev.compactmods.machines.CompactMachines;
import dev.compactmods.machines.api.dimension.CompactDimension;
import dev.compactmods.machines.api.dimension.MissingDimensionException;
import dev.compactmods.machines.location.LevelBlockPosition;
import dev.compactmods.machines.room.RoomHelper;
import dev.compactmods.machines.room.graph.CompactRoomProvider;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record PlayerRequestedTeleportPacket(LevelBlockPosition machine, String room) implements C2SPacket {

    public PlayerRequestedTeleportPacket(FriendlyByteBuf buf) {
        this(buf.readWithCodec(LevelBlockPosition.CODEC), buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeWithCodec(LevelBlockPosition.CODEC, machine);
        buf.writeUtf(room);
    }

    public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, SimpleChannel simpleChannel) {
        server.execute(() -> {
            if (player != null) {
                try {
                    final var provider = CompactRoomProvider.instance(CompactDimension.forServer(server));
                    provider.forRoom(room).ifPresent(info -> {
                        try {
                            RoomHelper.teleportPlayerIntoMachine(player.level, player, machine, info);
                        } catch (MissingDimensionException ignored) {
                        }
                    });
                } catch (MissingDimensionException e) {
                    CompactMachines.LOGGER.error("Failed to teleport player into machine.", e);
                }
            }
        });
    }
}
