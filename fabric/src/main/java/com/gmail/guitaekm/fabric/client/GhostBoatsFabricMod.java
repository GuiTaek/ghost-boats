package com.gmail.guitaekm.fabric.client;

import com.gmail.guitaekm.client.GhostBoatsMod;
import com.gmail.guitaekm.client.RecordingHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

@Environment(EnvType.CLIENT)
public final class GhostBoatsFabricMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(
                worldRenderContext -> GhostBoatsMod.renderBoat(
                    worldRenderContext.matrixStack(),
                    worldRenderContext.camera()
                ));
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(
                (minecraft, clientLevel) -> GhostBoatsMod.initWorld(clientLevel)
        );
        ClientTickEvents.END_CLIENT_TICK.register(
                minecraft -> RecordingHandler.onClientPostTick()
        );
    }
}
