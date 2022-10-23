package dev.compactmods.machines.api.room;

import dev.compactmods.machines.api.room.history.IRoomHistoryItem;
import io.github.fabricators_of_create.porting_lib.extensions.INBTSerializable;
import net.minecraft.nbt.ListTag;

public interface IRoomHistory<T extends IRoomHistoryItem> extends INBTSerializable<ListTag> {

    void clear();
    boolean hasHistory();
    T peek();
    T pop();

    void addHistory(T item);
}
