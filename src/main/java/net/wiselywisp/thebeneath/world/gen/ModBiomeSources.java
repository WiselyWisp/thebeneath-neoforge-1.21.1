package net.wiselywisp.thebeneath.world.gen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;


public final class ModBiomeSources {
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> TYPES =
            DeferredRegister.create(Registries.BIOME_SOURCE, TheBeneath.MOD_ID);

    static {
        TYPES.register("natural_beneath", () -> NaturalBeneathBiomeSource.CODEC);
    }

    private ModBiomeSources() {}
}
