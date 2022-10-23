package dev.compactmods.machines.tunnel.definitions;

import com.google.common.collect.ImmutableSet;
import dev.compactmods.machines.api.location.IDimensionalBlockPosition;
import dev.compactmods.machines.api.tunnels.TunnelDefinition;
import dev.compactmods.machines.api.tunnels.TunnelPosition;
import dev.compactmods.machines.api.tunnels.capability.CapabilityLookupTunnel;
import dev.compactmods.machines.api.tunnels.capability.CapabilityTunnel;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FastColor;

// WIP do not register yet
public class SmartItemTunnel implements TunnelDefinition, CapabilityLookupTunnel {
    @Override
    public int ringColor() {
        return FastColor.ARGB32.color(255, 205, 143, 36);
    }

    @Override
    public ImmutableSet<CapabilityTunnel.StorageType<?, ?>> getSupportedCapabilities() {
        return ImmutableSet.of(CapabilityTunnel.ITEM);
    }

    public Storage<ItemVariant> findCapability(
            MinecraftServer server, TunnelPosition tunnelPosition,
            IDimensionalBlockPosition targetPosition) {
        final var lev = targetPosition.level(server);
        if(!lev.isLoaded(targetPosition.getBlockPosition())) {
            return null;
        }

        return targetPosition.getBlockEntity(server)
                .map(be -> TransferUtil.getItemStorage(be, tunnelPosition.machineSide().getOpposite()))
                .orElse(null);
    }

}
