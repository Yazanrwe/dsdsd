package com.example.freelook.mixin;

import com.example.freelook.FreelookClient;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public class CameraMixin {

    /**
     * Replace the camera rotation (normally the player's yaw/pitch)
     * with the independent freelook rotation. Ordinal 0 is the first
     * setRotation call in Camera#update, before the camera position is computed.
     */
    @ModifyArgs(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V",
                    ordinal = 0
            )
    )
    private void freelook$modifyRotation(Args args) {
        if (FreelookClient.isActive()) {
            args.set(0, FreelookClient.getCameraYaw());
            args.set(1, FreelookClient.getCameraPitch());
        }
    }
}
