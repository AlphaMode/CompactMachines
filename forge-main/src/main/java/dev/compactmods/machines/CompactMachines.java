package dev.compactmods.machines;

import com.mojang.serialization.Lifecycle;
import dev.compactmods.machines.api.CompactMachinesAddon;
import dev.compactmods.machines.api.ICompactMachinesAddon;
import dev.compactmods.machines.api.core.CMRegistries;
import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.api.tunnels.capability.CapabilityTunnel;
import dev.compactmods.machines.command.Commands;
import dev.compactmods.machines.config.CommonConfig;
import dev.compactmods.machines.config.EnableVanillaRecipesConfigCondition;
import dev.compactmods.machines.config.ServerConfig;
import dev.compactmods.machines.core.*;
import dev.compactmods.machines.dimension.Dimension;
import dev.compactmods.machines.graph.Graph;
import dev.compactmods.machines.machine.Machines;
import dev.compactmods.machines.machine.block.CompactMachineBlockEntity;
import dev.compactmods.machines.room.RoomEventHandler;
import dev.compactmods.machines.room.Rooms;
import dev.compactmods.machines.room.data.LootFunctions;
import dev.compactmods.machines.room.network.RoomNetworkHandler;
import dev.compactmods.machines.shrinking.Shrinking;
import dev.compactmods.machines.tunnel.TunnelWallEntity;
import dev.compactmods.machines.tunnel.Tunnels;
import dev.compactmods.machines.upgrade.MachineRoomUpgrades;
import dev.compactmods.machines.wall.Walls;
import io.github.fabricators_of_create.porting_lib.util.LazyItemGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import team.reborn.energy.api.EnergyStorage;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CompactMachines implements ModInitializer {
    /**
     * @deprecated Switch usages to use api Constants in 1.20, eliminate it here
     */
    @Deprecated(forRemoval = true)
    public static final String MOD_ID = Constants.MOD_ID;

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Marker CONN_MARKER = MarkerManager.getMarker("cm_connections");
    public static final Marker ADDON_LIFECYCLE = MarkerManager.getMarker("addons");

    public static final CreativeModeTab COMPACT_MACHINES_ITEMS = new LazyItemGroup(Constants.MOD_ID) {
        @Override
        public @Nonnull
        ItemStack makeIcon() {
            return new ItemStack(Machines.MACHINE_BLOCK_ITEM_NORMAL.get());
        }
    };

    private static Set<ICompactMachinesAddon> loadedAddons;

    @Override
    public void onInitialize() {
        Registries.setup();
        preparePackages();
        doRegistration();

        // Configuration
        ModLoadingContext.registerConfig(Constants.MOD_ID, ModConfig.Type.COMMON, CommonConfig.CONFIG);
        ModLoadingContext.registerConfig(Constants.MOD_ID, ModConfig.Type.SERVER, ServerConfig.CONFIG);

        EnableVanillaRecipesConfigCondition.register();

        ModConfigEvent.LOADING.register(CommonConfig::onLoaded);
        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventHandler::onServerStarted);
        ServerWorldEvents.LOAD.register(ServerEventHandler::onWorldLoaded);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(ServerEventHandler::onPlayerDimChange);
        ServerPlayConnectionEvents.JOIN.register(ServerEventHandler::onPlayerLogin);
        CommonEventHandler.init();
        RoomEventHandler.init();
        ModBusEvents.setup();

        ((WritableRegistry)BuiltinRegistries.REGISTRY).register(CMRegistries.TEMPLATE_REG_KEY, Rooms.TEMPLATES.get(), Lifecycle.experimental());

        CompactMachinesNet.CHANNEL.initServerListener();
        RoomNetworkHandler.CHANNEL.initServerListener();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            CompactMachinesNet.CHANNEL.initClientListener();
            RoomNetworkHandler.CHANNEL.initClientListener();
        }

        ItemStorage.SIDED.registerForBlockEntities((blockEntity, context) -> {
            if (blockEntity instanceof CompactMachineBlockEntity machine)
                return machine.getTunnelContext(CapabilityTunnel.ITEM, context);
            if (blockEntity instanceof TunnelWallEntity tunnel)
                return tunnel.getCapability(CapabilityTunnel.ITEM, context);
            return null;
        }, Machines.MACHINE_TILE_ENTITY.get(), Tunnels.TUNNEL_BLOCK_ENTITY.get());
        FluidStorage.SIDED.registerForBlockEntities((blockEntity, context) -> {
            if (blockEntity instanceof CompactMachineBlockEntity machine)
                return machine.getTunnelContext(CapabilityTunnel.FLUID, context);
            if (blockEntity instanceof TunnelWallEntity tunnel)
                return tunnel.getCapability(CapabilityTunnel.FLUID, context);
            return null;
        }, Machines.MACHINE_TILE_ENTITY.get(), Tunnels.TUNNEL_BLOCK_ENTITY.get());
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, context) -> {
            if (blockEntity instanceof CompactMachineBlockEntity machine)
                return machine.getTunnelContext(CapabilityTunnel.ENERGY, context);
            if (blockEntity instanceof TunnelWallEntity tunnel)
                return tunnel.getCapability(CapabilityTunnel.ENERGY, context);
            return null;
        }, Machines.MACHINE_TILE_ENTITY.get(), Tunnels.TUNNEL_BLOCK_ENTITY.get());
    }

    /**
     * Sets up the deferred registration for usage in package/module setup.
     */
    private static void doRegistration() {
        Registries.BLOCKS.register();
        Registries.ITEMS.register();
        Registries.BLOCK_ENTITIES.register();
        Registries.TUNNEL_DEFINITIONS.register();
        Registries.CONTAINERS.register();
        Registries.ROOM_TEMPLATES.register();
        Registries.UPGRADES.register();
        Registries.NODE_TYPES.register();
        Registries.EDGE_TYPES.register();
        Registries.COMMAND_ARGUMENT_TYPES.register();
        Registries.LOOT_FUNCS.register();


        CompactMachines.loadedAddons = new HashSet<>(FabricLoader.getInstance().getEntrypoints("compact_machines_addon", ICompactMachinesAddon.class));

        CompactMachines.loadedAddons.forEach(addon -> {
            LOGGER.debug(ADDON_LIFECYCLE, "Sending registration hook to addon: {}", addon.getClass().getName());
            addon.afterRegistration();
        });
    }

    private static void preparePackages() {
        // Package initialization here, this kickstarts the rest of the DR code (classloading)
        Machines.prepare();
        Walls.prepare();
        Tunnels.prepare();
        Shrinking.prepare();

        UIRegistration.prepare();
        Dimension.prepare();
        Rooms.prepare();
        MachineRoomUpgrades.prepare();
        Graph.prepare();
        Commands.prepare();
        LootFunctions.prepare();
    }

    public static Set<ICompactMachinesAddon> getAddons() {
        return loadedAddons;
    }
}
