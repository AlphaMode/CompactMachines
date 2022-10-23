package dev.compactmods.machines.tunnel.definitions;

import com.google.common.collect.ImmutableSet;
import dev.compactmods.machines.api.tunnels.TunnelDefinition;
import dev.compactmods.machines.api.tunnels.capability.CapabilityTunnel;
import dev.compactmods.machines.api.tunnels.lifecycle.TunnelInstance;
import io.github.fabricators_of_create.porting_lib.extensions.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.FastColor;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import javax.annotation.Nonnull;

public class ForgeEnergyTunnel
        implements TunnelDefinition, CapabilityTunnel<ForgeEnergyTunnel.Instance> {
    @Override
    public int ringColor() {
        return FastColor.ARGB32.color(255, 0, 166, 88);
    }

    @Override
    public ImmutableSet<StorageType> getSupportedCapabilities() {
        return ImmutableSet.of(CapabilityTunnel.ENERGY);
    }

    @Override
    public EnergyStorage getCapability(StorageType type, Instance instance) {
        if (type == CapabilityTunnel.ENERGY) {
            return instance.getStorage();
        }

        return null;
    }

    @Override
    public Instance newInstance(BlockPos position, Direction side) {
        return new Instance();
    }

    public static class Instance implements TunnelInstance, INBTSerializable<CompoundTag> {

        private final long DEFAULT_STORAGE = 10000;
        private SimpleEnergyStorage storage;

        public Instance() {
            this.storage = new SimpleEnergyStorage(DEFAULT_STORAGE, DEFAULT_STORAGE, DEFAULT_STORAGE);
        }

        @Nonnull
        public EnergyStorage getStorage() {
            return this.storage;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag data = new CompoundTag();
            data.putLong("energy", storage.getAmount());
            return data;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            if(!nbt.contains("amount")) {
                this.storage = new SimpleEnergyStorage(DEFAULT_STORAGE, DEFAULT_STORAGE, DEFAULT_STORAGE);
                return;
            }
            if(nbt.contains("storage")) { // Load forge data if present
                Tag tag = nbt.get("storage");
                if (tag instanceof IntTag intTag)
                    storage.amount = intTag.getAsInt();
                return;
            }

            final var stor = nbt.getLong("energy");
            storage.amount = stor;
        }
    }
}
