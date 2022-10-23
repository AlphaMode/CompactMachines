package dev.compactmods.machines.core;

import dev.compactmods.machines.location.LevelBlockPosition;
import dev.compactmods.machines.room.menu.MachineRoomMenu;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.inventory.MenuType;

public class UIRegistration {

    public static final RegistryObject<MenuType<MachineRoomMenu>> MACHINE_MENU = Registries.CONTAINERS.register("machine", () -> new ExtendedScreenHandlerType<>(
            ((windowId, inv, data) -> {
                data.readBlockPos();
                final var mach = data.readWithCodec(LevelBlockPosition.CODEC);
                final var room = data.readUtf();
                final boolean hasName = data.readBoolean();
                final var roomName = hasName ? data.readUtf() : "Room Preview";

                return new MachineRoomMenu(windowId, room, mach, roomName);
            })
    ));

    public static void prepare() {

    }
}
