package com.gmail.guitaekm.neoforge;

import com.gmail.guitaekm.client.GhostBoatsMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

import static net.neoforged.fml.loading.FMLEnvironment.dist;


@Mod(value = GhostBoatsMod.MOD_ID, dist = Dist.CLIENT)
public final class GhostBoatsNeoForgeMod {
    public GhostBoatsNeoForgeMod(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(GhostBoatsNeoForgeMod::onRenderLevelStage);
        NeoForge.EVENT_BUS.addListener(GhostBoatsNeoForgeMod::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(GhostBoatsNeoForgeMod::onClientTickPost);
        modEventBus.addListener(GhostBoatsNeoForgeMod::registerKeyBindings);
        GhostBoatsMod.init();
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
        GhostBoatsMod.onClientPostTick();
    }
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(GhostBoatsMod.displayConfig);
        event.register(GhostBoatsMod.recordConfig);
        event.register(GhostBoatsMod.forceDisplay);
    }
}
