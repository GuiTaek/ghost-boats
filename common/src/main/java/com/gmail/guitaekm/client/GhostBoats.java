package com.gmail.guitaekm.client;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.system.Platform;

import java.util.function.Supplier;


public final class GhostBoats {
    public static final String MOD_ID = "ghost_boats";

    // heavily guided by ChatGPT
    public static void renderBoat(PoseStack poseStack, Camera camera) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        Entity boat = new Boat(EntityType.OAK_BOAT, minecraft.level, () -> Items.OAK_BOAT);
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
