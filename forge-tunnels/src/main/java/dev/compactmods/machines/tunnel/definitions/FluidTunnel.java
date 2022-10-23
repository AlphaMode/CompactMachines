package dev.compactmods.machines.tunnel.definitions;

import com.google.common.collect.ImmutableSet;
import dev.compactmods.machines.api.tunnels.TunnelDefinition;
import dev.compactmods.machines.api.tunnels.capability.CapabilityTunnel;
import dev.compactmods.machines.api.tunnels.lifecycle.TunnelInstance;
import io.github.fabricators_of_create.porting_lib.extensions.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FastColor;

import javax.annotation.Nonnull;

public class FluidTunnel
        implements TunnelDefinition, CapabilityTunnel<FluidTunnel.Instance> {
    @Override
    public int ringColor() {
        return FastColor.ARGB32.color(255, 0, 138, 224);
    }

    @Override
    public ImmutableSet<StorageType> getSupportedCapabilities() {
        return ImmutableSet.of(CapabilityTunnel.FLUID);
    }

    @Override
    public FluidTank getCapability(StorageType type, FluidTunnel.Instance instance) {
        if(type == CapabilityTunnel.FLUID)
            return instance.getHandler();

        return null;
    }

    @Override
    public FluidTunnel.Instance newInstance(BlockPos position, Direction side) {
        return new Instance(4000);
    }

    public class Instance implements TunnelInstance, INBTSerializable<CompoundTag> {

        private final FluidTank handler;

        public Instance(int size) {
            this.handler = new FluidTank(size);
        }

        @Nonnull
        private FluidTank getHandler() {
            return this.handler;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag data = new CompoundTag();
            return handler.writeToNBT(data);
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            handler.readFromNBT(nbt);
        }
    }
}
