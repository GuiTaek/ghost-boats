package com.gmail.guitaekm.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public final class GhostBoatsMod {
    public static final String MOD_ID = "ghost_boats";
    public static Entity boat;

    public static void initWorld(Level level) {
        if (level == null) {
            throw new IllegalStateException("minecraft.level should not be null");
        }
        boat = new Boat(EntityType.OAK_BOAT, level, () -> Items.OAK_BOAT);
    }

    // heavily guided by ChatGPT
    public static void renderBoat(PoseStack poseStack, Camera camera) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        boat.setPos(0, 128, 0);
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
