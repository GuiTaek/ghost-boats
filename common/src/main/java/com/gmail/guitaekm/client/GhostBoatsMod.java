package com.gmail.guitaekm.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;


public final class GhostBoatsMod {
    public static final String MOD_ID = "ghost_boats";
    public static final Logger LOGGER = LogManager.getLogger(GhostBoatsMod.MOD_ID);
    public static Entity boat;
    public static boolean boatVisible;
    public static KeyMapping displayConfig = new KeyMapping(
            "key.ghost_boats.display_config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "key.categories.ghost_boats"
    );
    public static boolean displayKeyPressed = false;
    public static KeyMapping recordConfig = new KeyMapping(
            "key.ghost_boats.record_config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.categories.ghost_boats"
    );
    public static void init() {
        RecordingHandler.init();
    }
    public static boolean recordKeyPressed = false;
    public static KeyMapping forceDisplay = new KeyMapping(
            "key.ghost_boats.force_display",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.ghost_boats"
    );
    public static boolean forceKeyPressed = false;
    public static void initWorld(Level level) {
        if (level == null) {
            throw new IllegalStateException("level should not be null");
        }
        boat = new Boat(EntityType.OAK_BOAT, level, () -> Items.OAK_BOAT);
        boatVisible = false;
    }
    public static void onClientPostTick() {
        handleKeys();
        RecordingHandler.onClientPostTick();
    }
    public static void handleKeys() {
        if (displayConfig.isDown()) {
            if (!displayKeyPressed) {
                onDisplayKeyPressed();
            }
            displayKeyPressed = true;
        } else {
            displayKeyPressed = false;
        }
        if (recordConfig.isDown()) {
            if (!recordKeyPressed) {
                onRecordKeyPressed();
            }
            recordKeyPressed = true;
        } else {
            recordKeyPressed = false;
        }
        if (forceDisplay.isDown()) {
            if (!forceKeyPressed) {
                onForceDisplayKeyPressed();
            }
            forceKeyPressed = true;
        } else {
            forceKeyPressed = false;
        }
    }

    public static void onDisplayKeyPressed() {
        Minecraft.getInstance().setScreen(new DisplayScreen());
    }

    public static void onRecordKeyPressed() {
        Minecraft.getInstance().setScreen(new RecordingScreen());
    }

    public static void onForceDisplayKeyPressed() {
        RecordingHandler.forcedDisplay = true;
    }

    // heavily guided by ChatGPT
    public static void renderBoat(PoseStack poseStack, Camera camera) {
        if (!boatVisible) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        Vec3 camPos = camera.getPosition();
        minecraft.getEntityRenderDispatcher().render(
                boat,
                boat.getX() - camPos.x, boat.getY() - camPos.y, boat.getZ() - camPos.z,
                camera.getPartialTickTime(),
                poseStack,
                minecraft.renderBuffers().bufferSource(),
                0xFFFFFF
        );
    }
}
