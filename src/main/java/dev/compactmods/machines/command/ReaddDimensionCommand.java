package dev.compactmods.machines.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.compactmods.machines.core.Registration;
import dev.compactmods.machines.util.DimensionUtil;
import dev.compactmods.machines.util.TranslationUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ReaddDimensionCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("registerdim")
                .requires(cs -> cs.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ReaddDimensionCommand::exec);
    }

    private static int exec(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var src = ctx.getSource();
        var serv = src.getServer();

        var compactLevel = serv.getLevel(Registration.COMPACT_DIMENSION);
        if (compactLevel == null) {
            src.sendSuccess(TranslationUtil.command("level_not_found").withStyle(ChatFormatting.RED), false);

            DimensionUtil.createAndRegisterWorldAndDimension(serv);
        } else {
            src.sendSuccess(TranslationUtil.command("level_registered").withStyle(ChatFormatting.DARK_GREEN), false);
        }

        return 0;
    }


}
