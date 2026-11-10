package net.wiselywisp.thebeneath.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, TheBeneath.MOD_ID);
    public static final ResourceLocation WOZZY_ID = ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "wozzy");
    public static final DeferredHolder<SoundEvent, SoundEvent> WOZZY =
            SOUNDS.register("wozzy", () -> SoundEvent.createVariableRangeEvent(WOZZY_ID));
    public static final ResourceLocation WANDERER_STATIC_ID =
            ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "wanderer_static");
    public static final DeferredHolder<SoundEvent, SoundEvent> WANDERER_STATIC =
            SOUNDS.register("wanderer_static", () -> SoundEvent.createVariableRangeEvent(WANDERER_STATIC_ID));
    public static final ResourceLocation ECHO_FOOTSTEP_ID =
            ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "echo_footstep");
    public static final DeferredHolder<SoundEvent, SoundEvent> ECHO_FOOTSTEP =
            SOUNDS.register("echo_footstep", () -> SoundEvent.createVariableRangeEvent(ECHO_FOOTSTEP_ID));
    public static final ResourceKey<JukeboxSong> WOZZY_KEY =
            ResourceKey.create(Registries.JUKEBOX_SONG, WOZZY_ID);
    private ModSounds() {}
}
