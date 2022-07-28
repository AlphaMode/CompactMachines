package dev.compactmods.machines.machine;

import dev.compactmods.machines.CompactMachines;
import dev.compactmods.machines.api.machine.MachineNbt;
import dev.compactmods.machines.api.room.IRoomInformation;
import dev.compactmods.machines.api.tunnels.capability.CapabilityTunnel;
import dev.compactmods.machines.location.LevelBlockPosition;
import dev.compactmods.machines.core.MissingDimensionException;
import dev.compactmods.machines.core.Registration;
import dev.compactmods.machines.machine.graph.DimensionMachineGraph;
import dev.compactmods.machines.machine.graph.CompactMachineNode;
import dev.compactmods.machines.machine.graph.legacy.LegacyMachineConnections;
import dev.compactmods.machines.room.graph.CompactMachineRoomNode;
import dev.compactmods.machines.tunnel.TunnelWallEntity;
import dev.compactmods.machines.tunnel.graph.TunnelConnectionGraph;
import dev.compactmods.machines.util.EnergyTransferable;
import io.github.fabricators_of_create.porting_lib.block.CustomUpdateTagHandlingBlockEntity;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;

public class CompactMachineBlockEntity extends BlockEntity implements CustomUpdateTagHandlingBlockEntity, FluidTransferable, ItemTransferable, EnergyTransferable {
    private static final String ROOM_NBT = "room_pos";
    private static final String LEGACY_MACH_ID = "machine_id";
    public long nextSpawnTick = 0;

    protected UUID owner;
    protected String schema;
    protected boolean locked = false;
    private ChunkPos roomChunk;
    private int legacyMachineId = -1;

    private WeakReference<CompactMachineNode> graphNode;
    private WeakReference<CompactMachineRoomNode> roomNode;

    public CompactMachineBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.MACHINE_TILE_ENTITY.get(), pos, state);
    }

    @Nullable
    @Override
    public Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        if(level instanceof ServerLevel sl) {
            return (Storage<FluidVariant>) getConnectedRoom().map(roomId -> {
                try {
                    final var serv = sl.getServer();
                    final var compactDim = serv.getLevel(Registration.COMPACT_DIMENSION);

                    final var graph = TunnelConnectionGraph.forRoom(compactDim, roomId);

                    final var supportingTunnels = graph.getTunnelsSupporting(getLevelPosition(), side, CapabilityTunnel.FLUID);
                    final var firstSupported = supportingTunnels.findFirst();
                    if (firstSupported.isEmpty())
                        return null;

                    final var compact = serv.getLevel(Registration.COMPACT_DIMENSION);
                    if (compact == null)
                        throw new MissingDimensionException();

                    if (compact.getBlockEntity(firstSupported.get()) instanceof TunnelWallEntity tunnel) {
                        return tunnel.getTunnelCapability(CapabilityTunnel.FLUID, side).getValueUnsafer();
                    } else {
                        return null;
                    }
                } catch (MissingDimensionException e) {
                    CompactMachines.LOGGER.fatal(e);
                    return null;
                }
            }).orElse(null);
        }
        return null;
    }

    @Nullable
    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        if(level instanceof ServerLevel sl) {
            return (Storage<ItemVariant>) getConnectedRoom().map(roomId -> {
                try {
                    final var serv = sl.getServer();
                    final var compactDim = serv.getLevel(Registration.COMPACT_DIMENSION);

                    final var graph = TunnelConnectionGraph.forRoom(compactDim, roomId);

                    final var supportingTunnels = graph.getTunnelsSupporting(getLevelPosition(), side, CapabilityTunnel.ITEM);
                    final var firstSupported = supportingTunnels.findFirst();
                    if (firstSupported.isEmpty())
                        return null;

                    final var compact = serv.getLevel(Registration.COMPACT_DIMENSION);
                    if (compact == null)
                        throw new MissingDimensionException();

                    if (compact.getBlockEntity(firstSupported.get()) instanceof TunnelWallEntity tunnel) {
                        return tunnel.getTunnelCapability(CapabilityTunnel.ITEM, side).getValueUnsafer();
                    } else {
                        return null;
                    }
                } catch (MissingDimensionException e) {
                    CompactMachines.LOGGER.fatal(e);
                    return null;
                }
            }).orElse(null);
        }

        return null;
    }

    @Override
    public EnergyStorage getEnergyStorage(@Nullable Direction side) {
        if(level instanceof ServerLevel sl) {
            return (EnergyStorage) getConnectedRoom().map(roomId -> {
                try {
                    final var serv = sl.getServer();
                    final var compactDim = serv.getLevel(Registration.COMPACT_DIMENSION);

                    final var graph = TunnelConnectionGraph.forRoom(compactDim, roomId);

                    final var supportingTunnels = graph.getTunnelsSupporting(getLevelPosition(), side, CapabilityTunnel.ENERGY);
                    final var firstSupported = supportingTunnels.findFirst();
                    if (firstSupported.isEmpty())
                        return null;

                    final var compact = serv.getLevel(Registration.COMPACT_DIMENSION);
                    if (compact == null)
                        throw new MissingDimensionException();

                    if (compact.getBlockEntity(firstSupported.get()) instanceof TunnelWallEntity tunnel) {
                        return tunnel.getTunnelCapability(CapabilityTunnel.ENERGY, side).getValueUnsafer();
                    } else {
                        return null;
                    }
                } catch (MissingDimensionException e) {
                    CompactMachines.LOGGER.fatal(e);
                    return null;
                }
            }).orElse(null);
        }

        return null;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.legacyMachineId != -1)
            this.updateLegacyData();

        this.syncConnectedRoom();
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);

        // TODO customName = nbt.getString("CustomName");
        if (nbt.contains(MachineNbt.OWNER)) {
            owner = nbt.getUUID(MachineNbt.OWNER);
        } else {
            owner = null;
        }

        if (nbt.contains(LEGACY_MACH_ID)) {
            this.legacyMachineId = nbt.getInt(LEGACY_MACH_ID);
        }

        nextSpawnTick = nbt.getLong("spawntick");
        if (nbt.contains("schema")) {
            schema = nbt.getString("schema");
        } else {
            schema = null;
        }

        if (nbt.contains("locked")) {
            locked = nbt.getBoolean("locked");
        } else {
            locked = false;
        }
    }

    private void updateLegacyData() {
        if (level instanceof ServerLevel sl) {
            try {
                final var legacy = LegacyMachineConnections.get(sl.getServer());

                DimensionMachineGraph graph = DimensionMachineGraph.forDimension(sl);
                graph.addMachine(worldPosition);

                final ChunkPos oldRoom = legacy.getConnectedRoom(this.legacyMachineId);
                CompactMachines.LOGGER.info(CompactMachines.CONN_MARKER, "Rebinding machine {} ({}/{}) to room {}", legacyMachineId, worldPosition, level.dimension(), roomChunk);

                this.roomChunk = oldRoom;
                graph.connectMachineToRoom(worldPosition, roomChunk);
            } catch (MissingDimensionException e) {
                CompactMachines.LOGGER.fatal(CompactMachines.CONN_MARKER, "Could not load connection info from legacy data; machine at {} in dimension {} will be unmapped.", worldPosition, level.dimension());
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        // nbt.putString("CustomName", customName.getString());

        if (owner != null) {
            nbt.putUUID(MachineNbt.OWNER, this.owner);
        }

        nbt.putLong("spawntick", nextSpawnTick);
        if (schema != null) {
            nbt.putString("schema", schema);
        }

        nbt.putBoolean("locked", locked);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag data = super.getUpdateTag();

        getConnectedRoom().ifPresent(room -> {
            data.putIntArray(ROOM_NBT, new int[]{room.x, room.z});
        });

        if (level instanceof ServerLevel) {
            // TODO - Internal player list
            if (this.owner != null)
                data.putUUID("owner", this.owner);
        }

        return data;
    }

    public Optional<ChunkPos> getConnectedRoom() {
        if (level instanceof ServerLevel sl) {
            if (roomChunk != null)
                return Optional.of(roomChunk);

            final var graph = DimensionMachineGraph.forDimension(sl);

            var chunk = graph.getConnectedRoom(worldPosition);
            chunk.ifPresent(c -> this.roomChunk = c);
            return chunk;
        }

        return Optional.ofNullable(roomChunk);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        CustomUpdateTagHandlingBlockEntity.super.handleUpdateTag(tag);

        if (tag.contains("players")) {
            CompoundTag players = tag.getCompound("players");
            // playerData = CompactMachinePlayerData.fromNBT(players);

        }

        if (tag.contains(ROOM_NBT)) {
            int[] room = tag.getIntArray(ROOM_NBT);
            this.roomChunk = new ChunkPos(room[0], room[1]);
        }

        if (tag.contains("owner"))
            owner = tag.getUUID("owner");
    }

    public Optional<UUID> getOwnerUUID() {
        return Optional.ofNullable(this.owner);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean hasPlayersInside() {
        // TODO
        return false;
    }

    public LevelBlockPosition getLevelPosition() {
        return new LevelBlockPosition(level.dimension(), worldPosition);
    }

    public void syncConnectedRoom() {
        if (this.level == null || this.level.isClientSide) return;

        if (level instanceof ServerLevel sl) {
            final var graph = DimensionMachineGraph.forDimension(sl);
            graph.getMachineNode(worldPosition).ifPresent(node -> {
                this.graphNode = new WeakReference<>(node);
            });

            this.getConnectedRoom()
                    .flatMap(room -> graph.getRoomNode(this.roomChunk))
                    .ifPresent(roomNode -> this.roomNode = new WeakReference<>(roomNode));

            this.setChanged();
        }
    }

    public void setConnectedRoom(ChunkPos room) {
        if(level instanceof ServerLevel sl) {
            final var dimMachines = DimensionMachineGraph.forDimension(sl);
            dimMachines.connectMachineToRoom(worldPosition, room);
            syncConnectedRoom();
        }
    }

    public void disconnect() {
        if(level instanceof ServerLevel sl) {
            final var dimMachines = DimensionMachineGraph.forDimension(sl);
            dimMachines.disconnect(worldPosition);

            this.roomChunk = null;
            this.graphNode.clear();
            setChanged();
        }
    }
}
