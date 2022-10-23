package dev.compactmods.machines.compat.curios;

import dev.compactmods.machines.api.core.Constants;
import dev.compactmods.machines.api.shrinking.PSDTags;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class CuriosCompat {

    private static final ResourceLocation CURIO_TEXTURE = new ResourceLocation(Constants.MOD_ID, "curios/empty_psd");

    private static boolean isPsd(ItemStack stack) {
        return stack.is(PSDTags.ITEM);
    }

    public static void addTextures(Consumer<ResourceLocation> stitch) {
        stitch.accept(CURIO_TEXTURE);
    }

    public static boolean hasPsdCurio(@Nonnull  LivingEntity ent) {
        return TrinketsApi.getTrinketComponent(ent).map(trinketComponent -> trinketComponent.isEquipped(CuriosCompat::isPsd)).orElse(false);
    }
}
