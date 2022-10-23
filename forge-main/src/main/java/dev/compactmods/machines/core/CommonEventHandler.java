package dev.compactmods.machines.core;

import dev.compactmods.machines.wall.ProtectedWallBlock;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CommonEventHandler {

    public static void init() {
        AttackBlockCallback.EVENT.register(CommonEventHandler::onLeftClickBlock);
    }

    public static InteractionResult onLeftClickBlock(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
        final var lev = player.getLevel();

        final var state = lev.getBlockState(pos);
        if(state.getBlock() instanceof ProtectedWallBlock pwb) {
            if(!pwb.canPlayerBreak(lev, player, pos))
                return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }
}
