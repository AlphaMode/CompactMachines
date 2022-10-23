package dev.compactmods.machines.compat.theoneprobe;

import dev.compactmods.machines.compat.theoneprobe.elements.PlayerFaceElement;
import dev.compactmods.machines.compat.theoneprobe.overrides.CompactMachineNameOverride;
import dev.compactmods.machines.compat.theoneprobe.providers.CompactMachineProvider;
import dev.compactmods.machines.compat.theoneprobe.providers.TunnelProvider;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbePlugin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class TheOneProbeMain implements ITheOneProbePlugin {
    @Override
    public void onLoad(ITheOneProbe PROBE) {
        PROBE.registerBlockDisplayOverride(new CompactMachineNameOverride());
        PROBE.registerProvider(new CompactMachineProvider());
        PROBE.registerProvider(new TunnelProvider());

        PROBE.registerElementFactory(new IElementFactory() {
            @Override
            public IElement createElement(FriendlyByteBuf buffer) {
                return new PlayerFaceElement(buffer.readGameProfile());
            }

            @Override
            public ResourceLocation getId() {
                return PlayerFaceElement.ID;
            }
        });
    }

}
