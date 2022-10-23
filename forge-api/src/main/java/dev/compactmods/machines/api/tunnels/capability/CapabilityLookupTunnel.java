package dev.compactmods.machines.api.tunnels.capability;

import com.google.common.collect.ImmutableSet;
import dev.compactmods.machines.api.location.IDimensionalBlockPosition;
import dev.compactmods.machines.api.tunnels.TunnelPosition;
import net.minecraft.server.MinecraftServer;

public interface CapabilityLookupTunnel {

    ImmutableSet<CapabilityTunnel.StorageType<?, ?>> getSupportedCapabilities();

    <T> T findCapability(MinecraftServer server, TunnelPosition tunnelPosition, IDimensionalBlockPosition connectedPosition);
}
