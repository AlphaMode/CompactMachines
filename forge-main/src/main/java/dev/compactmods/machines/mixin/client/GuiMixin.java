package dev.compactmods.machines.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.compactmods.machines.room.client.overlay.RoomMetadataDebugOverlay;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow private int screenWidth;

    @Shadow private int screenHeight;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
    private void compact$roommetadata(PoseStack poseStack, float partialTick, CallbackInfo ci) {
        RoomMetadataDebugOverlay.render((Gui) (Object) this, poseStack, partialTick, this.screenWidth, this.screenHeight);
    }
}
