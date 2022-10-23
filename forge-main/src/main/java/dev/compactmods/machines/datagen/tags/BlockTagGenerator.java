package dev.compactmods.machines.datagen.tags;

import dev.compactmods.machines.api.core.CMTags;
import dev.compactmods.machines.machine.Machines;
import dev.compactmods.machines.machine.block.LegacySizedCompactMachineBlock;
import dev.compactmods.machines.wall.Walls;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {

    public BlockTagGenerator(FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    @SuppressWarnings("removal")
    public void generateTags() {
        var legacySizedMachines = Set.of(Machines.MACHINE_BLOCK_TINY.get(),
                Machines.MACHINE_BLOCK_SMALL.get(),
                Machines.MACHINE_BLOCK_NORMAL.get(),
                Machines.MACHINE_BLOCK_LARGE.get(),
                Machines.MACHINE_BLOCK_GIANT.get(),
                Machines.MACHINE_BLOCK_MAXIMUM.get());

        var legacyMachines = tag(LegacySizedCompactMachineBlock.LEGACY_MACHINES_TAG);
        var allMachines = tag(CMTags.MACHINE_BLOCK);
        var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        var ironTool = tag(BlockTags.NEEDS_IRON_TOOL);

        var breakableWall = Walls.BLOCK_BREAKABLE_WALL.get();
        pickaxe.add(breakableWall);
        ironTool.add(breakableWall);

        legacySizedMachines.forEach(mach -> {
            legacyMachines.add(mach);
            allMachines.add(mach);
            pickaxe.add(mach);
            ironTool.add(mach);
        });

        Block machine = Machines.MACHINE_BLOCK.get();
        allMachines.add(machine);
        pickaxe.add(machine);
        ironTool.add(machine);
    }
}