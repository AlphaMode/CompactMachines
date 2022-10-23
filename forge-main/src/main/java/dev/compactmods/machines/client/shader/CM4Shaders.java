package dev.compactmods.machines.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.fabricators_of_create.porting_lib.event.client.RegisterShadersCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

import static dev.compactmods.machines.api.core.Constants.MOD_ID;

public class CM4Shaders
{
    private static ShaderInstance blockFullbrightShader;
    private static ShaderInstance wallShader;

    public static void registerShaders(ResourceManager resourceManager, RegisterShadersCallback.ShaderRegistry registry) throws IOException
    {
        registry.registerShader(
                new ShaderInstance(resourceManager, new ResourceLocation(MOD_ID, "block_fullbright").toString(), DefaultVertexFormat.BLOCK),
                shader -> blockFullbrightShader = shader
        );

        registry.registerShader(
                new ShaderInstance(resourceManager, new ResourceLocation(MOD_ID, "wall").toString(), DefaultVertexFormat.BLOCK),
                shader -> wallShader = shader
        );
    }

    public static ShaderInstance wall() { return wallShader; }
    public static ShaderInstance fullbright()
    {
        return blockFullbrightShader;
    }
}
