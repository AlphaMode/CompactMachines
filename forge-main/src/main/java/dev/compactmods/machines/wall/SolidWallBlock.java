package dev.compactmods.machines.wall;

import dev.compactmods.machines.config.ServerConfig;
import io.github.fabricators_of_create.porting_lib.block.ValidSpawnBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class SolidWallBlock extends ProtectedWallBlock implements ValidSpawnBlock {
    public SolidWallBlock(Properties props) {
        super(props);
    }

    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter world, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return pos.getY() == ServerConfig.MACHINE_FLOOR_Y.get();
    }

    @Override // TODO: REMOVE
    public boolean isToolEffective(BlockState state, DiggerItem tool) {
        return false;
    }
}
