package dev.compactmods.machines.room.network;

import dev.compactmods.machines.api.dimension.MissingDimensionException;
import dev.compactmods.machines.room.Rooms;
import dev.compactmods.machines.room.exceptions.NonexistentRoomException;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public record PlayerStartedRoomTrackingPacket(String room) implements C2SPacket {

    public PlayerStartedRoomTrackingPacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(room);
    }

    public void handle(MinecraftServer server, ServerPlayer sender, ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, SimpleChannel simpleChannel) {
        server.execute(() -> {
            StructureTemplate blocks = null;
            try {
                blocks = Rooms.getInternalBlocks(sender.server, room).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            } catch (MissingDimensionException e) {
                throw new RuntimeException(e);
            } catch (NonexistentRoomException e) {
                throw new RuntimeException(e);
            }
            RoomNetworkHandler.CHANNEL.sendToClient(new InitialRoomBlockDataPacket(blocks), sender);
        });
    }
}
