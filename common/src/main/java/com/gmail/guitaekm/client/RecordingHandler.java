package com.gmail.guitaekm.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RecordingHandler {
    public static ResourceLocation writeLocation = ResourceLocation.fromNamespaceAndPath("minecraft", "save");
    public static ResourceLocation readLocation = null;
    public static List<Pair<Vec3, Float>> recording = new ArrayList<>();

    public static void onClientPostTick() {
        Minecraft minecraft = Minecraft.getInstance();
        RecordingHandler.handleRead();
        RecordingHandler.handleWrite(minecraft);
    }

    public static void handleRead() {
        if (readLocation == null) {
            return;
        }
    }

    public static void handleWrite(Minecraft minecraft) {
        if (writeLocation == null) {
            return;
        }
        if (minecraft.player == null) {
            // world not loaded yet
            return;
        }
        if (minecraft.player.getVehicle() == null) {
            if (recording.isEmpty()) {
                return;
            }
            // guided by ChatGPT
            Path savePath = Paths
                    .get("generated")
                    .resolve(writeLocation.getNamespace())
                    .resolve(writeLocation.getPath() + ".txt");
            try {
                Files.createDirectories(savePath.getParent());
                Writer writer = Files.newBufferedWriter(savePath);
                for (Pair<Vec3, Float> line : recording) {
                    writer.write(
                            line.getFirst().x + ";"
                                    + line.getFirst().y + ";"
                                    + line.getFirst().z + ";"
                                    + line.getSecond() + "\n"
                    );
                }
                writer.close();
            } catch (IOException e) {
                GhostBoatsMod.LOGGER.warn("Saving of recorded Boat path failed. Check permissions");
            }
            recording.clear();
            return;
        }
        if (minecraft.player.getVehicle().getType().is(EntityTypeTags.BOAT)) {
            recording.add(
                    new Pair<>(
                            minecraft.player.getVehicle().position(),
                            minecraft.player.getVehicle().getXRot()
                    )
            );
        }
    }


}
