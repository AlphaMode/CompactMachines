package dev.compactmods.machines.upgrade;

import dev.compactmods.machines.CompactMachines;
import dev.compactmods.machines.api.room.upgrade.RoomUpgrade;
import dev.compactmods.machines.core.Registries;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class MachineRoomUpgrades {

    public static final Supplier<Registry<RoomUpgrade>> REGISTRY = Registries.UPGRADES.makeRegistry();

    // ================================================================================================================
    public static final RegistryObject<RoomUpgrade> CHUNKLOAD = Registries.UPGRADES.register(ChunkloadUpgrade.REG_ID.getPath(), ChunkloadUpgrade::new);

    public static final RegistryObject<Item> CHUNKLOADER = Registries.ITEMS.register("chunkloader_upgrade", () -> new ChunkloadUpgradeItem(new Item.Properties()
            .tab(CompactMachines.COMPACT_MACHINES_ITEMS)
            .stacksTo(1)));

    public static void prepare() {

    }
}
