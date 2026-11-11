
package net.wiselywisp.thebeneath.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class OldNoteScreen extends Screen {

    private static final ResourceLocation NOTE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "thebeneath",
                    "textures/gui/old_note.png"
            );

    private static final int TEXTURE_WIDTH = 1254;
    private static final int TEXTURE_HEIGHT = 1254;

    public OldNoteScreen() {
        super(Component.literal("Old Note"));
    }

    @Override
    public void render(GuiGraphics graphics,
                       int mouseX, int mouseY, float partialTick) {

        graphics.fill(0, 0, this.width, this.height, 0xAA000000);

        float scale = Math.min(
                1.0f,
                Math.min(
                        (float) (this.width - 32) / TEXTURE_WIDTH,
                        (float) (this.height - 32) / TEXTURE_HEIGHT
                )
        );

        int drawWidth = Math.max(
                1, Math.round(TEXTURE_WIDTH * scale)
        );

        int drawHeight = Math.max(
                1, Math.round(TEXTURE_HEIGHT * scale)
        );

        int x = (this.width - drawWidth) / 2;
        int y = (this.height - drawHeight) / 2;

        graphics.blit(
                NOTE_TEXTURE,
                x, y,
                drawWidth, drawHeight,
                0, 0,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
    }
}
