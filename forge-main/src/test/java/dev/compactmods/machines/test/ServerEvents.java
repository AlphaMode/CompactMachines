package dev.compactmods.machines.test;

import dev.compactmods.machines.CompactMachines;
import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.api.dimension.CompactDimension;
import dev.compactmods.machines.util.DimensionUtil;
import net.minecraft.server.MinecraftServer;

public class ServerEvents {

    public static void onServerStarted(final MinecraftServer serv) {
        var compactLevel = serv.getLevel(CompactDimension.LEVEL_KEY);
        if (compactLevel == null) {
            CompactMachines.LOGGER.warn("Compact dimension not found; recreating it.");
            DimensionUtil.createAndRegisterWorldAndDimension(serv);
        }
    }
}
