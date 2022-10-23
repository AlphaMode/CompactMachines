package dev.compactmods.machines.tunnel.network;

import dev.compactmods.machines.api.tunnels.TunnelDefinition;
import dev.compactmods.machines.tunnel.Tunnels;
import dev.compactmods.machines.tunnel.client.ClientTunnelHandler;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TunnelAddedPacket implements S2CPacket {

    @Nonnull
    private final BlockPos position;

    @Nonnull
    private final TunnelDefinition type;

    public TunnelAddedPacket(@Nonnull BlockPos tunnelPos, @Nonnull TunnelDefinition tunnelType) {
        this.position = tunnelPos;
        this.type = tunnelType;
    }

    public TunnelAddedPacket(FriendlyByteBuf buf) {
        position = buf.readBlockPos();
        type = Tunnels.getDefinition(buf.readResourceLocation());
    }

    public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        client.execute(() -> {
            ClientTunnelHandler.setTunnel(position, type);
        });
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.position);
        buf.writeResourceLocation(Objects.requireNonNull(Tunnels.getRegistryId(this.type)));
    }
}
