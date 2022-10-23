package dev.compactmods.machines.room.data;

import dev.compactmods.machines.core.Registries;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class LootFunctions {

    public static RegistryObject<LootItemFunctionType> COPY_ROOM_BINDING = Registries.LOOT_FUNCS.register("copy_room_binding",
            () -> new LootItemFunctionType(new CopyRoomBindingFunction.Serializer()));

    public static void prepare() {

    }
}
