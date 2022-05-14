package dev.compactmods.machines.tunnel.definitions;

import com.google.common.collect.ImmutableSet;
import dev.compactmods.machines.api.tunnels.TunnelDefinition;
import dev.compactmods.machines.api.tunnels.TunnelPosition;
import dev.compactmods.machines.api.tunnels.capability.CapabilityTunnel;
import dev.compactmods.machines.api.tunnels.lifecycle.TunnelInstance;
import dev.compactmods.machines.api.tunnels.lifecycle.TunnelTeardownHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FastColor;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemTunnel
        implements TunnelDefinition, CapabilityTunnel<ItemTunnel.Instance>, TunnelTeardownHandler<ItemTunnel.Instance> {

    @Override
    public int ringColor() {
        return FastColor.ARGB32.color(255, 205, 143, 36);
    }

    /**
     * Handle initialization tasks for the tunnel's data here.
     */
    public Instance newInstance(BlockPos pos, Direction side) {
        return new Instance(10);
    }

    @Override
    public ImmutableSet<StorageType> getSupportedCapabilities() {
        return ImmutableSet.of(StorageType.ITEM);
    }

    /**
     * Fetch a capability instance from a tunnel.
     *
     * @param capType Capability type. See implementations like {@link IItemHandler} as a reference.
     * @return LazyOptional instance of the capability, or LO.empty otherwise.
     */
    @Override
    public <CapType> LazyOptional<CapType> getCapability(StorageType type, Instance instance) {
        if (type == StorageType.ITEM) {
            return instance.lazy();
        }

        return LazyOptional.empty();
    }

    /**
     * Drops items into the machine room before the tunnel is removed from the wall.
     *
     * @param instance The tunnel instance being modified.
     */
    @Override
    public void onRemoved(TunnelPosition position, Instance instance) {
        BlockPos dropAt = position.pos().relative(position.side());

        NonNullList<ItemStack> stacks = NonNullList.create();
        for (int i = 0; i < instance.handler.getSlots(); i++) {
            ItemStack stack = instance.handler.getStackInSlot(i);
            if (!stack.isEmpty())
                stacks.add(stack);
        }

        Containers.dropContents(position.level(), dropAt, stacks);
    }

    public static class Instance implements TunnelInstance, INBTSerializable<CompoundTag> {

        private final LazyOptional<ItemStackHandler> laze;
        final ItemStackHandler handler;

        public Instance(int buffer) {
            this.handler = new ItemStackHandler(buffer);
            this.laze = LazyOptional.of(this::getItems);
        }

        private @Nonnull
        ItemStackHandler getItems() {
            return handler;
        }

        public <CapType> LazyOptional<CapType> lazy() {
            return laze.cast();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put("items", handler.serializeNBT());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            handler.deserializeNBT(nbt.getCompound("items"));
        }
    }
}
