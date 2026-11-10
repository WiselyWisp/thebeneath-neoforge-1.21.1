package net.wiselywisp.thebeneath.world.gen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;

public final class ModChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> TYPES =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, TheBeneath.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<? extends ChunkGenerator>> BIOME_TERRAIN =
            TYPES.register("biome_terrain", () -> BiomeTerrainGenerator.CODEC);
    private ModChunkGenerators() {}
}
