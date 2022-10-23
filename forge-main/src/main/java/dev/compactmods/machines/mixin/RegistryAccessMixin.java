package dev.compactmods.machines.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import dev.compactmods.machines.api.core.CMRegistries;
import dev.compactmods.machines.api.room.RoomTemplate;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RegistryAccess.class)
public abstract interface RegistryAccessMixin {
    @Shadow
    private static <E> void put(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder, ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec, Codec<E> codec2) {
    }

    @Inject(method = "method_30531", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistryAccess;put(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Codec;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void compactmachines$addTemplateRegistry(CallbackInfoReturnable<ImmutableMap> cir, ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder) {
        put(builder, CMRegistries.TEMPLATE_REG_KEY, RoomTemplate.CODEC, RoomTemplate.CODEC);
    }
}
