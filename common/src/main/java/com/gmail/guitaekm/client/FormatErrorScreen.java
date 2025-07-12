package com.gmail.guitaekm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FormatErrorScreen extends Screen {
    protected Screen oldScreen;
    protected int ERROR_WIDTH;
    protected int ERROR_HEIGHT;
    protected int ERROR_X;
    protected int ERROR_Y;
    public static int WIDGET_HEIGHT = 20;
    public static int WIDGET_SPACING = 3;
    public static int SCREEN_HEIGHT = 2 * WIDGET_HEIGHT + WIDGET_SPACING;
    public Button button;
    public Component errorMessage;

    public FormatErrorScreen(Screen oldScreen) {
        super(oldScreen.getTitle());
        this.oldScreen = oldScreen;
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
        this.errorMessage = Component.translatable("desc.ghost_boats.format_error");
        Component OK_COMPONENT = Component.translatable("gui.ok");

        this.ERROR_WIDTH = this.font.width(this.errorMessage.getString());
        this.ERROR_X = this.width / 2 - this.ERROR_WIDTH / 2;
        this.ERROR_Y = this.height / 2 - SCREEN_HEIGHT / 2;

        int OK_WIDTH = this.font.width(OK_COMPONENT.getString()) + 5;
        int OK_X = this.width / 2 - OK_WIDTH / 2;
        int OK_Y = this.height / 2- SCREEN_HEIGHT / 2 + 20 + WIDGET_SPACING;
        this.button.setRectangle(OK_WIDTH, WIDGET_HEIGHT, OK_X, OK_Y);
        this.button.setMessage(OK_COMPONENT);
    }

    @Override
    protected void init() {
        this.button = Button.builder(Component.literal(""), (btn) -> {
            System.out.println("button ok clicked");
            this.onClose();
        }).bounds(0, 0, 1, 1).build();
        this.redraw();
        this.addRenderableWidget(this.button);
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
    }

}
