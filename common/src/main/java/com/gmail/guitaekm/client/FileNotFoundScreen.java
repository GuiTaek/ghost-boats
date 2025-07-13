package com.gmail.guitaekm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FileNotFoundScreen extends Screen {

    protected String path;
    protected Screen oldScreen;
    protected int ERROR_WIDTH;
    protected int ERROR_X;
    protected int ERROR_Y;
    protected int PATH_WIDTH;
    protected int PATH_X;
    protected int PATH_Y;
    public static int OK_HEIGHT = 20;
    public static int TEXT_HEIGHT = 12;
    public static int WIDGET_SPACING = 3;
    public static int SCREEN_HEIGHT = OK_HEIGHT + 2 * TEXT_HEIGHT + 2 * WIDGET_SPACING;
    public Button button;
    public Component errorMessage;
    public Component pathMessage;

    public FileNotFoundScreen(Screen oldScreen, String path) {
        super(oldScreen.getTitle());
        this.oldScreen = oldScreen;
        this.path = path;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.oldScreen);
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
        this.errorMessage = Component.translatable("desc.ghost_boats.file_not_found_error");
        this.pathMessage = Component.literal(this.path);
        Component OK_COMPONENT = Component.translatable("gui.ok");

        this.ERROR_WIDTH = this.font.width(this.errorMessage.getString());
        this.ERROR_X = this.width / 2 - this.ERROR_WIDTH / 2;
        this.ERROR_Y = this.height / 2 - SCREEN_HEIGHT / 2;

        this.PATH_X = this.width / 2 - this.PATH_WIDTH / 2;
        this.PATH_Y = this.height / 2 - SCREEN_HEIGHT / 2 + TEXT_HEIGHT + WIDGET_SPACING;
        int OK_WIDTH = this.font.width(OK_COMPONENT.getString()) + 5;
        int OK_X = this.width / 2 - OK_WIDTH / 2;
        int OK_Y = this.height / 2 - SCREEN_HEIGHT / 2 + 2 * (TEXT_HEIGHT + WIDGET_SPACING);
        this.button.setRectangle(OK_WIDTH, OK_HEIGHT, OK_X, OK_Y);
        this.button.setMessage(OK_COMPONENT);
    }

    @Override
    protected void init() {
        this.button = Button.builder(Component.literal(""), (btn) -> {
            this.onClose();
        }).bounds(0, 0, 1, 1).build();
        this.redraw();
        this.addRenderableWidget(this.button);
        this.PATH_WIDTH = this.font.width(this.path);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.redraw();
        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow
        context.drawString(this.font, this.errorMessage, this.ERROR_X, this.ERROR_Y, 0xFFFFFFFF, true);
        context.drawString(this.font, this.pathMessage, this.PATH_X, this.PATH_Y, 0xFFFFFFFF, true);
    }
}
