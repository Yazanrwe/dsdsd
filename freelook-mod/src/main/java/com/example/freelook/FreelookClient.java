package com.example.freelook;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class FreelookClient implements ClientModInitializer {
    private static KeyBinding keyBinding;
    private static FreelookConfig config;

    private static boolean active = false;
    private static float cameraYaw;
    private static float cameraPitch;
    private static Perspective previousPerspective;
    private static boolean perspectiveChanged = false;

    @Override
    public void onInitializeClient() {
        config = FreelookConfig.load();

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freelook.activate",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "key.categories.freelook"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(FreelookClient::tick);
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            if (active) {
                stop(client);
            }
            return;
        }

        if (config.toggle) {
            while (keyBinding.wasPressed()) {
                if (active) {
                    stop(client);
                } else if (client.currentScreen == null) {
                    start(client);
                }
            }
        } else {
            // drain queued presses, we only care about the held state
            while (keyBinding.wasPressed()) {
                // no-op
            }
            boolean held = keyBinding.isPressed();
            if (held && !active && client.currentScreen == null) {
                start(client);
            } else if (!held && active) {
                stop(client);
            }
        }

        // Opening any screen (inventory, chat, pause) ends freelook
        if (active && client.currentScreen != null) {
            stop(client);
        }
    }

    private static void start(MinecraftClient client) {
        cameraYaw = client.player.getYaw();
        cameraPitch = client.player.getPitch();
        active = true;

        if (config.forceThirdPerson) {
            previousPerspective = client.options.getPerspective();
            if (previousPerspective != Perspective.THIRD_PERSON_BACK) {
                client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                perspectiveChanged = true;
            }
        }
    }

    private static void stop(MinecraftClient client) {
        active = false;
        if (perspectiveChanged && previousPerspective != null) {
            client.options.setPerspective(previousPerspective);
        }
        perspectiveChanged = false;
        previousPerspective = null;
    }

    /** Same math as Entity#changeLookDirection, but applied to the camera only. */
    public static void rotateCamera(double deltaX, double deltaY) {
        cameraPitch += (float) deltaY * 0.15F;
        cameraYaw += (float) deltaX * 0.15F;
        cameraPitch = MathHelper.clamp(cameraPitch, -90.0F, 90.0F);
    }

    public static boolean isActive() {
        return active;
    }

    public static float getCameraYaw() {
        return cameraYaw;
    }

    public static float getCameraPitch() {
        return cameraPitch;
    }
}
