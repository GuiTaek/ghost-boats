package com.gmail.guitaekm.neoforge;

import com.gmail.guitaekm.client.GhostBoatEntityRenderer;
import com.gmail.guitaekm.client.GhostBoats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeEventHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


@Mod(value = com.gmail.guitaekm.client.GhostBoats.MOD_ID, dist = Dist.CLIENT)
public final class GhostBoatsNeoForgeMod {
    public GhostBoatsNeoForgeMod() {
        NeoForge.EVENT_BUS.addListener(GhostBoatsNeoForgeMod::onRenderLevelStage);
    }
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_ENTITIES)) {
            return;
        }
        GhostBoats.renderBoat(event.getPoseStack(), event.getCamera());
    }
}
