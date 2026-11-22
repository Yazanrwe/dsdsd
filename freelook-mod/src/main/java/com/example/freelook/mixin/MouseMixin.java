package com.example.freelook.mixin;

import com.example.freelook.FreelookClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {

    /**
     * While freelook is active, mouse movement rotates the camera
     * instead of the player.
     */
    @Redirect(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
            )
    )
    private void freelook$redirectLookDirection(ClientPlayerEntity player, double deltaX, double deltaY) {
        if (FreelookClient.isActive()) {
            FreelookClient.rotateCamera(deltaX, deltaY);
        } else {
            player.changeLookDirection(deltaX, deltaY);
        }
    }
}
