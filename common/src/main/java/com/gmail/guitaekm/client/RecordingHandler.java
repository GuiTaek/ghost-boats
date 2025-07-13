package com.gmail.guitaekm.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.phys.Vec3;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordingHandler {
    public static ResourceLocation writeLocation = ResourceLocation.fromNamespaceAndPath("ghost_boats", "save");
    public static ResourceLocation readLocation = ResourceLocation.fromNamespaceAndPath("ghost_boats", "load");
    public static List<Pair<Vec3, Float>> recordingWrite = new ArrayList<>();
    public static List<Pair<Vec3, Float>> recordingRead = new ArrayList<>();
    public static int readFrameNr = -1;
    private static final String NUMBER_REGEX = "[+-]?[0-9]+\\.[0-9]+(E[+-]?[0-9]+)?";
    private static boolean brokenFile = false;
    public static boolean forcedDisplay = false;
    public static Pattern STRING_TO_VALUES_REGEX = Pattern.compile("(%s);(%s);(%s);(%s);(%s)".formatted(
            NUMBER_REGEX,
            NUMBER_REGEX,
            NUMBER_REGEX,
            NUMBER_REGEX,
            NUMBER_REGEX
    ));
    public enum OverrideState {
        ALWAYS,
        RECORDING,
        DISPLAY

    }
    public static void init() {
        RecordingHandler.overrideState = OverrideState.ALWAYS;
    }
    public static OverrideState overrideState;

    public static Path getPath(ResourceLocation location) {
        return Paths
                .get("generated")
                .resolve(location.getNamespace())
                .resolve(location.getPath() + ".txt");
    }

    public static void onClientPostTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused()) {
            return;
        }
        RecordingHandler.handleRead(minecraft);
        RecordingHandler.handleWrite(minecraft);
    }

    // exists for debugging reasons
    private static int counterDebug = 0;
    private static void handleReadDebug(Minecraft minecraft) {
        if (minecraft.player == null) {
            return;
        }
        GhostBoatsMod.boat.setOldPosAndRot();
        GhostBoatsMod.boat.setPos(new Vec3(0, 128 + (double) counterDebug / 100, 0));
        counterDebug++;
        GhostBoatsMod.boatVisible = true;
    }

    protected static boolean recordingFaster(Path path) {
        try {
            // https://stackoverflow.com/a/1277904/3289974
            BufferedReader reader = new BufferedReader(new FileReader(path.toString()));
            int lines = 0;
            while (reader.readLine() != null) lines++;
            if (recordingWrite.size() > lines) {
                return false;
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            GhostBoatsMod.LOGGER.warn("Recording couldn't be saved because it couldn't be read for the override mode \"recording\"");
            return false;
        }
        return true;
    }

    public static void handleRead(Minecraft minecraft) {
        if (readLocation == null) {
            return;
        }
        if (minecraft.player == null) {
            // world not loaded yet
            return;
        }
        if (minecraft.player.getVehicle() == null && !forcedDisplay) {
            recordingRead.clear();
            brokenFile = false;
            readFrameNr = -1;
            GhostBoatsMod.boatVisible = false;
            return;
        }
        if (minecraft.player.getVehicle() != null && forcedDisplay) {
            forcedDisplay = false;
        }
        if (readFrameNr >= recordingRead.size()) {
            GhostBoatsMod.boatVisible = false;
            forcedDisplay = false;
            return;
        }
        GhostBoatsMod.boatVisible = true;
        if (readFrameNr == -1) {
            Path readPath = getPath(RecordingHandler.readLocation);
            String readPathString = readPath.toString();
            // from https://stackoverflow.com/a/4716623/3289974
            try(BufferedReader bufferedReader = new BufferedReader((new FileReader(readPathString)))) {
                String line = bufferedReader.readLine();
                int frameNr = -1;
                while (line != null) {
                    Matcher matcher = RecordingHandler.STRING_TO_VALUES_REGEX.matcher(line);
                    if (!matcher.matches() && !brokenFile) {
                        GhostBoatsMod.LOGGER.warn("File has wrong format. File will be ignored.");
                        brokenFile = true;
                        return;
                    }
                    if (!matcher.matches() && brokenFile) {
                        return;
                    }
                    brokenFile = false;
                    if (frameNr > Math.round(Double.parseDouble(matcher.group(1)) * 20)) {
                        GhostBoatsMod.LOGGER.warn("The time stamps of the file are decreasing. This will be ignored by the mod.");
                    }
                    double x = Double.parseDouble(matcher.group(3));
                    double y = Double.parseDouble(matcher.group(5));
                    double z = Double.parseDouble(matcher.group(7));
                    float rotX = Float.parseFloat(matcher.group(9));
                    recordingRead.add(new Pair<>(new Vec3(x, y, z), rotX));
                    frameNr++;
                    line = bufferedReader.readLine();
                }
            } catch (FileNotFoundException e) {
                GhostBoatsMod.LOGGER.warn("There doesn't exist such a file: %s".formatted(readPathString));
            } catch (IOException e) {
                GhostBoatsMod.LOGGER.warn("Problems opening the file. Check permissions.");
            }
        }
        ++readFrameNr;
        if (readFrameNr >= recordingRead.size()) {
            return;
        }
        GhostBoatsMod.boat.setOldPosAndRot();
        GhostBoatsMod.boat.setPos(recordingRead.get(readFrameNr).getFirst());
        GhostBoatsMod.boat.setYRot(recordingRead.get(readFrameNr).getSecond());
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
            if (recordingWrite.isEmpty()) {
                return;
            }
            // guided by ChatGPT
            Path savePath = getPath(writeLocation);
            if (overrideState == OverrideState.RECORDING) {
                if (!recordingFaster(savePath)) {
                    recordingWrite.clear();
                    return;
                }
            }
            if (overrideState == OverrideState.DISPLAY) {
                if (!recordingFaster(getPath(readLocation))) {
                    recordingWrite.clear();
                    return;
                }
            }
            try {
                Files.createDirectories(savePath.getParent());
                Writer writer = Files.newBufferedWriter(savePath);
                int frameNR = 0;
                for (Pair<Vec3, Float> line : recordingWrite) {
                    writer.write(
                            ((double)Math.round((double)frameNR / 20 * 10_000)) / 10_000 + ";"
                                    + line.getFirst().x + ";"
                                    + line.getFirst().y + ";"
                                    + line.getFirst().z + ";"
                                    + line.getSecond() + "\n"
                    );
                    frameNR++;
                }
                writer.close();
            } catch (IOException e) {
                GhostBoatsMod.LOGGER.warn("Saving of recorded Boat path failed. Check permissions");
            }
            recordingWrite.clear();
            return;
        }
        if (minecraft.player.getVehicle().getType().is(EntityTypeTags.BOAT)) {
            recordingWrite.add(
                    new Pair<>(
                            minecraft.player.getVehicle().position(),
                            minecraft.player.getVehicle().getYRot()
                    )
            );
        }
    }


}
