package dev.compactmods.machines.api.tunnels.capability;

import com.google.common.collect.ImmutableSet;
import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.api.tunnels.lifecycle.InstancedTunnel;
import dev.compactmods.machines.api.tunnels.lifecycle.TunnelInstance;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import team.reborn.energy.api.EnergyStorage;

/**
 *
 * @param <Tunnel>
 */
public interface CapabilityTunnel<Tunnel extends TunnelInstance> extends InstancedTunnel<Tunnel> {
    Registry<StorageType> STORAGE_TYPES = FabricRegistryBuilder.createSimple(StorageType.class, Constants.STORAGE_TYPE_REGISTRY).buildAndRegister();

    ImmutableSet<StorageType> getSupportedCapabilities();

    /**
     * Fetch a capability instance from a tunnel.
     * @param type Capability type. See implementations like {@link IItemHandler} as a reference.
     * @param <CapType> Type of capability to fetch off tunnel.
     * @return LazyOptional instance of the capability, or LO.empty otherwise.
     */
    <CapType> CapType getCapability(StorageType type, Tunnel instance);

    record StorageType<A, C>(BlockApiLookup<A, C> context) {
    }

    StorageType<Storage<ItemVariant>, Direction> ITEM = Registry.register(STORAGE_TYPES, Constants.ITEM_TUNNEL_KEY, new StorageType<>(ItemStorage.SIDED));
    StorageType<Storage<FluidVariant>, Direction> FLUID = Registry.register(STORAGE_TYPES, Constants.FLUID_TUNNEL_KEY, new StorageType<>(FluidStorage.SIDED));
    StorageType<EnergyStorage, Direction> ENERGY = Registry.register(STORAGE_TYPES, Constants.ENERGY_TUNNEL_KEY, new StorageType<>(EnergyStorage.SIDED));

}
