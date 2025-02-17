package dev.compactmods.machines.tunnel;

import dev.compactmods.machines.api.CompactMachinesAddon;
import dev.compactmods.machines.api.ICompactMachinesAddon;
import dev.compactmods.machines.api.core.CMRegistries;
import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.api.room.IRoomOwnerLookup;
import dev.compactmods.machines.api.room.registration.IRoomSpawnLookup;
import dev.compactmods.machines.api.tunnels.TunnelDefinition;
import dev.compactmods.machines.tunnel.definitions.BufferedItemTunnel;
import dev.compactmods.machines.tunnel.definitions.FluidTunnel;
import dev.compactmods.machines.tunnel.definitions.ForgeEnergyTunnel;
import dev.compactmods.machines.tunnel.definitions.redstone.RedstoneInTunnelDefinition;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;

import java.util.function.Supplier;

@CompactMachinesAddon
public class BuiltinTunnels implements ICompactMachinesAddon {
    public static Supplier<IRoomOwnerLookup> roomOwnerLookup;
    public static Supplier<IRoomSpawnLookup> roomSpawnLookup;

    public static LazyRegistrar<TunnelDefinition> DR = LazyRegistrar.create(CMRegistries.TYPES_REG_KEY, Constants.MOD_ID);

    // ================================================================================================================
    //   TUNNEL TYPE DEFINITIONS
    // ================================================================================================================
    public static final RegistryObject<TunnelDefinition> ITEM_TUNNEL_DEF = DR.register("item", BufferedItemTunnel::new);

    public static final RegistryObject<TunnelDefinition> FLUID_TUNNEL_DEF = DR.register("fluid", FluidTunnel::new);

    public static final RegistryObject<TunnelDefinition> FORGE_ENERGY = DR.register("energy", ForgeEnergyTunnel::new);

    public static final RegistryObject<TunnelDefinition> REDSTONE_IN = DR.register("redstone_in", RedstoneInTunnelDefinition::new);

    @Override
    public void afterRegistration() {
        DR.register();
    }

    public void acceptRoomOwnerLookup(Supplier<IRoomOwnerLookup> ownerLookup) {
        roomOwnerLookup = ownerLookup;
    }

    @Override
    public void acceptRoomSpawnLookup(Supplier<IRoomSpawnLookup> spawnLookup) {
        roomSpawnLookup = spawnLookup;
    }
}
