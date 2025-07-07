package com.gmail.guitaekm.fabric.client;

import com.gmail.guitaekm.client.GhostBoats;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

@Environment(EnvType.CLIENT)
public final class GhostBoatsFabricMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(new WorldRenderEvents.AfterEntities() {
            @Override
            public void afterEntities(WorldRenderContext worldRenderContext) {
                GhostBoats.renderBoat(
                        worldRenderContext.matrixStack(),
                        worldRenderContext.camera()
                );
            }
        });
    }
}
