package dev.compactmods.machines.core;

import dev.compactmods.machines.api.core.CMRegistries;
import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.api.room.RoomTemplate;
import dev.compactmods.machines.api.room.upgrade.RoomUpgrade;
import dev.compactmods.machines.api.tunnels.TunnelDefinition;
import dev.compactmods.machines.graph.IGraphEdgeType;
import dev.compactmods.machines.graph.IGraphNodeType;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import org.jetbrains.annotations.ApiStatus;

import static dev.compactmods.machines.api.core.Constants.MOD_ID;

public class Registries {

    // Machines, Walls, Shrinking
    public static final LazyRegistrar<Block> BLOCKS = LazyRegistrar.create(Registry.BLOCK, MOD_ID);
    public static final LazyRegistrar<Item> ITEMS = LazyRegistrar.create(Registry.ITEM, MOD_ID);
    public static final LazyRegistrar<BlockEntityType<?>> BLOCK_ENTITIES = LazyRegistrar.create(Registry.BLOCK_ENTITY_TYPE, MOD_ID);

    // Tunnels
    public static final LazyRegistrar<TunnelDefinition> TUNNEL_DEFINITIONS = LazyRegistrar.create(CMRegistries.TYPES_REG_KEY, MOD_ID);

    // UIRegistration
    public static final LazyRegistrar<MenuType<?>> CONTAINERS = LazyRegistrar.create(Registry.MENU, MOD_ID);

    // MachineRoomUpgrades
    public static final LazyRegistrar<RoomUpgrade> UPGRADES = LazyRegistrar.create(CMRegistries.ROOM_UPGRADES_REG_KEY, MOD_ID);

    // Graph
    @ApiStatus.Internal
    public static final ResourceKey<Registry<IGraphNodeType<?>>> NODES_REG_KEY = ResourceKey
            .createRegistryKey(new ResourceLocation(MOD_ID, "graph_nodes"));

    @ApiStatus.Internal
    public static final ResourceKey<Registry<IGraphEdgeType<?>>> EDGES_REG_KEY = ResourceKey
            .createRegistryKey(new ResourceLocation(MOD_ID, "graph_edges"));
    public static final LazyRegistrar<IGraphNodeType<?>> NODE_TYPES = LazyRegistrar.create(NODES_REG_KEY, MOD_ID);
    public static final LazyRegistrar<IGraphEdgeType<?>> EDGE_TYPES = LazyRegistrar.create(EDGES_REG_KEY, MOD_ID);

    // Commands
    public static final LazyRegistrar<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = LazyRegistrar.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, MOD_ID);

    // LootFunctions
    public static final LazyRegistrar<LootItemFunctionType> LOOT_FUNCS = LazyRegistrar.create(Registry.LOOT_FUNCTION_REGISTRY, MOD_ID);

    public static LazyRegistrar<RoomTemplate> ROOM_TEMPLATES = LazyRegistrar
            .create(CMRegistries.TEMPLATE_REG_KEY, Constants.MOD_ID);

    public static void setup() {

    }
}
