package dev.compactmods.machines.wall;

import io.github.fabricators_of_create.porting_lib.block.EntityDestroyBlock;
import io.github.fabricators_of_create.porting_lib.block.HarvestableBlock;
import io.github.fabricators_of_create.porting_lib.block.PlayerDestroyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;

public abstract class ProtectedWallBlock extends Block implements HarvestableBlock, EntityDestroyBlock, PlayerDestroyBlock {
    protected ProtectedWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(!canPlayerBreak(level, player, pos))
            return false;

        level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        return level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    public boolean canPlayerBreak(Level level, Player player, BlockPos pos) {
        if(!player.isCreative()) return false;
        if(!player.isShiftKeyDown()) return false;

        return true;
    }
}
