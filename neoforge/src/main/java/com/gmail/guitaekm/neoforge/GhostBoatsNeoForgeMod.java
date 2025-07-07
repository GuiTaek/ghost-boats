package com.gmail.guitaekm.neoforge;

import com.gmail.guitaekm.client.GhostBoatsMod;
import com.gmail.guitaekm.client.RecordingHandler;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;


@Mod(value = GhostBoatsMod.MOD_ID, dist = Dist.CLIENT)
public final class GhostBoatsNeoForgeMod {
    public GhostBoatsNeoForgeMod() {
        NeoForge.EVENT_BUS.addListener(GhostBoatsNeoForgeMod::onRenderLevelStage);
        NeoForge.EVENT_BUS.addListener(GhostBoatsNeoForgeMod::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(GhostBoatsNeoForgeMod::onClientTickPost);
    }
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_ENTITIES)) {
            return;
        }
        GhostBoatsMod.renderBoat(event.getPoseStack(), event.getCamera());
    }
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        GhostBoatsMod.initWorld((Level) event.getLevel());
    }
    public static void onClientTickPost(ClientTickEvent.Post event) {
        RecordingHandler.onClientPostTick();
    }
}
