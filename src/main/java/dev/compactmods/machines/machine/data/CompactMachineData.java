package dev.compactmods.machines.machine.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.compactmods.machines.CompactMachines;
import dev.compactmods.machines.api.codec.NbtListCollector;
import dev.compactmods.machines.api.location.IDimensionalPosition;
import dev.compactmods.machines.core.LevelBlockPosition;
import dev.compactmods.machines.core.MissingDimensionException;
import dev.compactmods.machines.core.Registration;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Holds information on the external points of a machine, ie the actual machine blocks.
 *
 * @deprecated This data should move to the connection graph or to the machine blocks, to be removed in 1.19
 */
@Deprecated(forRemoval = true, since = "4.0.7")
public class CompactMachineData extends SavedData {

    /**
     * File storage name.
     */
    public final static String DATA_NAME = CompactMachines.MOD_ID + "_machines";

    /**
     * Specifies locations of machine blocks, ie in the overworld.
     * This is used for things like tunnel handling and forced ejections.
     */
    private final Map<Integer, MachineData> data = new HashMap<>();
    private final Map<Integer, LazyOptional<IDimensionalPosition>> locations = new HashMap<>();


    @Nonnull
    public static CompactMachineData get(MinecraftServer server) throws MissingDimensionException {
        ServerLevel compactWorld = server.getLevel(Registration.COMPACT_DIMENSION);
        if (compactWorld == null) {
            CompactMachines.LOGGER.error("No compact dimension found. Report this.");
            throw new MissingDimensionException();
        }

        DimensionDataStorage sd = compactWorld.getDataStorage();
        return sd.computeIfAbsent(CompactMachineData::fromNbt, CompactMachineData::new, DATA_NAME);
    }

    public static CompactMachineData fromNbt(CompoundTag nbt) {
        CompactMachineData machines = new CompactMachineData();
        if (nbt.contains("locations")) {
            ListTag nbtLocations = nbt.getList("locations", Tag.TAG_COMPOUND);
            nbtLocations.forEach(nbtLoc -> {
                DataResult<MachineData> res = MachineData.CODEC.parse(NbtOps.INSTANCE, nbtLoc);
                res.resultOrPartial(err -> CompactMachines.LOGGER.error("Error while processing machine data: " + err))
                        .ifPresent(machineInfo -> machines.data.put(machineInfo.machineId, machineInfo));
            });
        }

        return machines;
    }

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag nbt) {
        if (!data.isEmpty()) {
            ListTag nbtLocations = data.values()
                    .stream()
                    .map(entry -> {
                        DataResult<Tag> nbtRes = MachineData.CODEC.encodeStart(NbtOps.INSTANCE, entry);
                        return nbtRes.resultOrPartial(err -> CompactMachines.LOGGER.error("Error serializing machine data: " + err));
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(NbtListCollector.toNbtList());

            nbt.put("locations", nbtLocations);
        }

        return nbt;
    }

    public void setMachineLocation(int machineId, LevelBlockPosition position) {
        // TODO - Packet/Event for machine changing external location (tunnels)
        if (data.containsKey(machineId)) {
            data.get(machineId).setLocation(position);
        } else {
            data.put(machineId, new MachineData(machineId, position));
        }

        if (locations.containsKey(machineId)) {
            locations.get(machineId).invalidate();
            locations.remove(machineId);
        }

        this.setDirty();
    }

    public LazyOptional<IDimensionalPosition> getMachineLocation(int machineId) {
        if (!data.containsKey(machineId))
            return LazyOptional.empty();

        if (locations.containsKey(machineId))
            return locations.get(machineId);

        var lazy = LazyOptional.of(() -> {
            MachineData machineData = this.data.get(machineId);
            return (IDimensionalPosition) machineData.location;
        });

        locations.put(machineId, lazy);
        return lazy;
    }

    public void remove(int id) {
        data.remove(id);
        locations.remove(id);
        setDirty();
    }

    public Stream<MachineData> stream() {
        return data.values().stream();
    }

    public int getNextMachineId() {
        // TODO - Optimize for gaps, in-memory during data loading process
        int i = 1;
        while (true) {
            if(data.containsKey(i)){
                i++;
                continue;
            }

            return i;
        }
    }

    public static class MachineData {
        private final int machineId;
        public LevelBlockPosition location;

        public static final Codec<MachineData> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("machine").forGetter(MachineData::getMachineId),
                LevelBlockPosition.CODEC.fieldOf("location").forGetter(MachineData::getLocation)
        ).apply(i, MachineData::new));

        public MachineData(int machineId, LevelBlockPosition location) {
            this.machineId = machineId;
            this.location = location;
        }

        public int getMachineId() {
            return this.machineId;
        }

        public LevelBlockPosition getLocation() {
            return this.location;
        }

        public void setLocation(LevelBlockPosition position) {
            this.location = position;
        }
    }
}
