package dev.compactmods.machines.client;

import dev.compactmods.machines.client.shader.CM4Shaders;
import dev.compactmods.machines.compat.curios.CuriosCompat;
import dev.compactmods.machines.core.UIRegistration;
import dev.compactmods.machines.machine.Machines;
import dev.compactmods.machines.machine.client.MachineColors;
import dev.compactmods.machines.room.client.MachineRoomScreen;
import dev.compactmods.machines.tunnel.Tunnels;
import dev.compactmods.machines.tunnel.client.TunnelColors;
import dev.compactmods.machines.tunnel.client.TunnelItemColor;
import io.github.fabricators_of_create.porting_lib.event.client.RegisterShadersCallback;
import io.github.fabricators_of_create.porting_lib.event.client.TextureStitchCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ClientEventHandler implements ClientModInitializer {

    public static void onItemColors() {
        ColorProviderRegistry.ITEM.register(new TunnelItemColor(), Tunnels.ITEM_TUNNEL.get());
        ColorProviderRegistry.ITEM.register(MachineColors.ITEM, Machines.BOUND_MACHINE_BLOCK_ITEM.get());
        ColorProviderRegistry.ITEM.register(MachineColors.ITEM, Machines.UNBOUND_MACHINE_BLOCK_ITEM.get());
    }

    public static void onBlockColors() {
        ColorProviderRegistry.BLOCK.register(new TunnelColors(), Tunnels.BLOCK_TUNNEL_WALL.get());
        ColorProviderRegistry.BLOCK.register(MachineColors.BLOCK, Machines.MACHINE_BLOCK.get());
    }

    @Override
    public void onInitializeClient() {
        RenderType cutout = RenderType.cutoutMipped();
        BlockRenderLayerMap.INSTANCE.putBlock(Tunnels.BLOCK_TUNNEL_WALL.get(), cutout);

        MenuScreens.register(UIRegistration.MACHINE_MENU.get(), MachineRoomScreen::new);

        onItemColors();
        onBlockColors();
        RegisterShadersCallback.EVENT.register(CM4Shaders::registerShaders);
        TextureStitchCallback.PRE.register(ClientEventHandler::onTextureStitch);
    }

    public static void onTextureStitch(TextureAtlas atlas, Consumer<ResourceLocation> adder) {
        if(FabricLoader.getInstance().isModLoaded("curios"))
            CuriosCompat.addTextures(adder);
    }
}
