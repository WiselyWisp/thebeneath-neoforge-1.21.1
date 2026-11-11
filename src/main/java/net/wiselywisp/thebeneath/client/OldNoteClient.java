package net.wiselywisp.thebeneath.client;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public final class OldNoteClient {

    private OldNoteClient() {

    }
    public static void openScreen() {
        Minecraft minecraft = Minecraft.getInstance();


        if (minecraft.player != null) {
            minecraft.player.playSound(
                    SoundEvents.BOOK_PAGE_TURN,
                    0.6F,
                    0.8F
            );
        }

        minecraft.setScreen(new OldNoteScreen());
    }
}