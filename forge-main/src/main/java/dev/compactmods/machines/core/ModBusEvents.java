package dev.compactmods.machines.core;

import dev.compactmods.machines.CompactMachines;
import dev.compactmods.machines.advancement.AdvancementTriggers;
import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.room.network.RoomNetworkHandler;

public class ModBusEvents {

    public static void setup() {
        CompactMachines.LOGGER.trace("Initializing network handler.");
        CompactMachinesNet.setupMessages();
        RoomNetworkHandler.setupMessages();

        CompactMachines.LOGGER.trace("Registering advancement triggers.");
        AdvancementTriggers.init();
    }
}
