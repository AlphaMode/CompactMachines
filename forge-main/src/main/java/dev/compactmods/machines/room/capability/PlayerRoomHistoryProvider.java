package dev.compactmods.machines.room.capability;

import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

public class PlayerRoomHistoryProvider implements PlayerComponent<PlayerRoomHistoryProvider> {

    private final CMRoomHistory history;

    public PlayerRoomHistoryProvider(Player player) {
        this.history = new CMRoomHistory();
    }

    @Override
    public void writeToNbt(CompoundTag nbt) {
        nbt.put("history", history.serializeNBT());
    }

    @Override
    public void readFromNbt(CompoundTag nbt) {
        if(nbt.contains("history")) {
            history.clear();
            history.deserializeNBT(nbt.getList("history", Tag.TAG_COMPOUND));
        }
    }

    public CMRoomHistory getHistory() {
        return history;
    }
}
