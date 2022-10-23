package dev.compactmods.machines.room.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.function.Supplier;

public record InitialRoomBlockDataPacket(StructureTemplate blocks) implements S2CPacket {

    public InitialRoomBlockDataPacket(FriendlyByteBuf buf) {
        this(readFromBuffer(buf));
    }

    public static StructureTemplate readFromBuffer(FriendlyByteBuf buf) {
        final var nbt = buf.readNbt();
        final var struct = new StructureTemplate();
        struct.load(nbt);
        return struct;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        final var tag = blocks.save(new CompoundTag());
        buf.writeNbt(tag);
    }

    public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        client.execute(() -> ClientRoomNetworkHandler.handleBlockData(this));
    }
}
