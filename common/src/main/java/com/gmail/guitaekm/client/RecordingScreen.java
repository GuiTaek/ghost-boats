package com.gmail.guitaekm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordingScreen extends Screen {
    // the way minecraft does it is shitty
    public static Pattern resourceNavigationRegex = Pattern.compile("([a-z0-9_.-]+):([a-z0-9_.-]+)");
    public static int EDIT_WIDTH = 200;
    public static int WIDGET_HEIGHT = 20;
    public static int WIDGET_SPACING = 3;
    public static int SCREEN_HEIGHT = 3 * WIDGET_HEIGHT + 2 * WIDGET_SPACING;
    public Button button;
    public EditBox editBox;
    public RecordingHandler.OverrideState temporaryOverrideState;
    public CycleButton<RecordingHandler.OverrideState> overrideStateButton;
    public Component stateAlways;
    public Component stateRecording;
    public Component stateDisplay;
    public Component overrideStateDesc;
    public RecordingScreen() {
        super(Component.translatable("key.ghost_boats.record_config"));
    }

    protected void redraw() {
        // can change ingame
        Component OK_COMPONENT = Component.translatable("gui.ok");

        int OK_WIDTH = this.font.width(OK_COMPONENT.getString()) + 5;
        int EDIT_X = this.width / 2 - EDIT_WIDTH / 2;
        int EDIT_Y = this.height / 2 - SCREEN_HEIGHT / 2;
        this.editBox.setRectangle(EDIT_WIDTH, WIDGET_HEIGHT, EDIT_X, EDIT_Y);

        int CYCLE_WIDTH = this.font.width(overrideStateDesc.getString())
                + this.font.width(": ")
                +  Utils.max(
                    this.font.width(stateAlways),
                    this.font.width(stateRecording),
                    this.font.width(stateDisplay)
                // no idea why it doesn't work without that
                ) + 5;
        int CYCLE_X = this.width / 2 - CYCLE_WIDTH / 2;
        int CYCLE_Y = this.height / 2 - SCREEN_HEIGHT / 2 + WIDGET_HEIGHT + WIDGET_SPACING;
        this.overrideStateButton.setRectangle(CYCLE_WIDTH, WIDGET_HEIGHT, CYCLE_X, CYCLE_Y);


        int OK_X = this.width / 2 - OK_WIDTH / 2;
        int OK_Y = this.height / 2 - SCREEN_HEIGHT / 2 + 2 * WIDGET_HEIGHT + 2 * WIDGET_SPACING;
        this.button.setRectangle(OK_WIDTH, WIDGET_HEIGHT, OK_X, OK_Y);
        this.button.setMessage(OK_COMPONENT);
    }

    @Override
    protected void init() {
        this.temporaryOverrideState = RecordingHandler.overrideState;
        this.stateAlways = Component.translatable("gui.ghost_boats.cycle_button_always");
        this.stateRecording = Component.translatable("gui.ghost_boats.cycle_button_recording");
        this.stateDisplay = Component.translatable("gui.ghost_boats.cycle_button_display");
        this.overrideStateDesc = Component.translatable("gui.ghost_boats.cycle_button_desc");
        overrideStateButton = CycleButton.<RecordingHandler.OverrideState>builder(state -> {
                            if (state == RecordingHandler.OverrideState.ALWAYS) {
                                return this.stateAlways;
                            } else if (state == RecordingHandler.OverrideState.RECORDING) {
                                return this.stateRecording;
                            } else {
                                return this.stateDisplay;
                            }
                        })
                        .withValues(
                                RecordingHandler.OverrideState.ALWAYS,
                                RecordingHandler.OverrideState.RECORDING,
                                RecordingHandler.OverrideState.DISPLAY
                        )
                        .create(
                                0, 0, 1, 1,
                                this.overrideStateDesc,
                                (cycleButtonx, overrideState) -> RecordingHandler.overrideState = overrideState
                        );
        this.overrideStateButton.setValue(this.temporaryOverrideState);
        this.editBox = new EditBox(
                this.font,
                0,
                0,
                1,
                1,
                Component.nullToEmpty(null)
        );
        this.editBox.setValue(RecordingHandler.writeLocation.toString());
        this.button = Button.builder(Component.literal(""), (button) -> {
            String userInput = this.editBox.getValue();
            Matcher matcher = resourceNavigationRegex.matcher(userInput);
            if (!matcher.matches()) {
                Minecraft.getInstance().setScreen(new FormatErrorScreen(this));
                return;
            }
            ResourceLocation newWriteLocation = ResourceLocation.fromNamespaceAndPath(matcher.group(1), matcher.group(2));
            Path savePath = RecordingHandler.getPath(newWriteLocation);
            try {
                Files.createDirectories(savePath.getParent());
            } catch (IOException e) {
                GhostBoatsMod.LOGGER.warn("following IO Error occured while generating the folders of path: %s".formatted(savePath.toAbsolutePath().toString()), e);
            }
            RecordingHandler.writeLocation = newWriteLocation;
            this.onClose();
        }).bounds(0, 0, 1, 1).build();
        this.redraw();
        this.addRenderableWidget(this.editBox);
        this.addRenderableWidget(this.button);
        this.addRenderableWidget(this.overrideStateButton);
        this.editBox.onClick(this.editBox.getX() + this.editBox.getInnerWidth(), this.editBox.getY() + (double) this.editBox.getHeight() / 2);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.redraw();
        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow
        // context.drawString(this.font, "Special Button", 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }
}