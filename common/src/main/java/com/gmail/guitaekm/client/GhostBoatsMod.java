package com.gmail.guitaekm.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerRegistry;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public final class GhostBoatsMod {
    public static final String MOD_ID = "ghost_boats";
    public static final Logger LOGGER = LogManager.getLogger(GhostBoatsMod.MOD_ID);
    public static Entity boat;
    public static boolean boatVisible;
    public static void initWorld(Level level) {
        if (level == null) {
            throw new IllegalStateException("minecraft.level should not be null");
        }
        boat = new Boat(EntityType.OAK_BOAT, level, () -> Items.OAK_BOAT);
        boatVisible = false;
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
