package dev.compactmods.machines.room.server;

import dev.compactmods.machines.api.core.CMRegistries;
import dev.compactmods.machines.api.room.RoomTemplate;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import net.minecraft.core.Registry;

public class RoomServerHelper {
    public static Registry<RoomTemplate> getTemplates() {
        return ServerLifecycleHooks.getCurrentServer()
                .registryAccess()
                .registryOrThrow(CMRegistries.TEMPLATE_REG_KEY);
    }
}
