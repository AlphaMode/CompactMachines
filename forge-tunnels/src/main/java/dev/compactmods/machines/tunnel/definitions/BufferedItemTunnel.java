package dev.compactmods.machines.tunnel.definitions;

import com.google.common.collect.ImmutableSet;
import dev.compactmods.machines.api.dimension.CompactDimension;
import dev.compactmods.machines.api.tunnels.TunnelDefinition;
import dev.compactmods.machines.api.tunnels.TunnelPosition;
import dev.compactmods.machines.api.tunnels.capability.CapabilityTunnel;
import dev.compactmods.machines.api.tunnels.lifecycle.TunnelInstance;
import dev.compactmods.machines.api.tunnels.lifecycle.TunnelTeardownHandler;
import io.github.fabricators_of_create.porting_lib.extensions.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FastColor;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class BufferedItemTunnel
        implements TunnelDefinition, CapabilityTunnel<BufferedItemTunnel.Instance>,
        TunnelTeardownHandler<BufferedItemTunnel.Instance> {

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
        return ImmutableSet.of(CapabilityTunnel.ITEM);
    }

    /**
     * Fetch a capability instance from a tunnel.
     *
     * @param type Capability type. See implementations like {@link IItemHandler} as a reference.
     * @return LazyOptional instance of the capability, or LO.empty otherwise.
     */
    @Override
    public  ItemStackHandler getCapability(StorageType type, Instance instance) {
        if (type == CapabilityTunnel.ITEM) {
            return instance.getItems();
        }

        return null;
    }

    /**
     * Drops items into the machine room before the tunnel is removed from the wall.
     *
     * @param instance The tunnel instance being modified.
     */
    @Override
    public void onRemoved(MinecraftServer server, TunnelPosition position, Instance instance) {
        BlockPos dropAt = position.pos().relative(position.wallSide(), 1);

        NonNullList<ItemStack> stacks = NonNullList.create();
        for (int i = 0; i < instance.handler.getSlots(); i++) {
            ItemStack stack = instance.handler.getStackInSlot(i);
            if (!stack.isEmpty())
                stacks.add(stack);
        }

        final var compactDim = server.getLevel(CompactDimension.LEVEL_KEY);
        if(compactDim != null)
            Containers.dropContents(compactDim, dropAt, stacks);
    }

    public static class Instance implements TunnelInstance, INBTSerializable<CompoundTag> {

        final ItemStackHandler handler;

        public Instance(int buffer) {
            this.handler = new ItemStackHandler(buffer);
        }

        private @Nonnull
        ItemStackHandler getItems() {
            return handler;
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
