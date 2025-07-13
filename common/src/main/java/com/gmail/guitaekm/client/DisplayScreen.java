package com.gmail.guitaekm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayScreen extends Screen {
    // the way minecraft does it is shitty
    public static Pattern resourceNavigationRegex = Pattern.compile("([a-z0-9_.-]+):([a-z0-9_.-]+)");
    public static int EDIT_WIDTH = 200;
    public static int WIDGET_HEIGHT = 20;
    public static int WIDGET_SPACING = 3;
    public static int SCREEN_HEIGHT = 2 * WIDGET_HEIGHT + WIDGET_SPACING;
    public Button button;
    public EditBox editBox;
    public DisplayScreen() {
        super(Component.translatable("key.ghost_boats.display_config"));
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    protected void redraw() {
        // can change ingame
        Component OK_COMPONENT = Component.translatable("gui.ok");

        int OK_WIDTH = this.font.width(OK_COMPONENT.getString()) + 5;
        int EDIT_X = this.width / 2 - EDIT_WIDTH / 2;
        int EDIT_Y = this.height / 2 - SCREEN_HEIGHT / 2;
        this.editBox.setRectangle(EDIT_WIDTH, WIDGET_HEIGHT, EDIT_X, EDIT_Y);

        int OK_X = this.width / 2 - OK_WIDTH / 2;
        int OK_Y = this.height / 2- SCREEN_HEIGHT / 2 + 20 + WIDGET_SPACING;
        this.button.setRectangle(OK_WIDTH, WIDGET_HEIGHT, OK_X, OK_Y);
        this.button.setMessage(OK_COMPONENT);
    }

    @Override
    protected void init() {
        this.editBox = new EditBox(
                this.font,
                0,
                0,
                1,
                1,
                Component.nullToEmpty(null)
        );
        this.editBox.setValue(RecordingHandler.readLocation.toString());
        this.button = Button.builder(Component.literal(""), (button) -> {
            String userInput = this.editBox.getValue();
            Matcher matcher = resourceNavigationRegex.matcher(userInput);
            if (!matcher.matches()) {
                Minecraft.getInstance().setScreen(new FormatErrorScreen(this));
                return;
            }
            ResourceLocation newReadLocation = ResourceLocation.fromNamespaceAndPath(matcher.group(1), matcher.group(2));
            Path path = RecordingHandler.getPath(newReadLocation);
            if (!path.toFile().exists()) {
                Minecraft.getInstance().setScreen(new FileNotFoundScreen(this, Paths.get(".minecraft").resolve(path).toString().toString()));
                return;
            }
            RecordingHandler.readLocation = newReadLocation;
            this.onClose();
        }).bounds(0, 0, 1, 1).build();
        this.redraw();
        this.addRenderableWidget(this.editBox);
        this.addRenderableWidget(this.button);
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