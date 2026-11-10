package net.wiselywisp.thebeneath.world.gen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.server.level.WorldGenRegion;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class BiomeTerrainGenerator extends ChunkGenerator {
    public static final MapCodec<BiomeTerrainGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource))
                    .apply(instance, BiomeTerrainGenerator::new));

    public BiomeTerrainGenerator(BiomeSource source) { super(source); }

    @Override protected MapCodec<? extends ChunkGenerator> codec() { return CODEC; }

    private boolean isLost(int x, int z, RandomState random) {
        Holder<Biome> biome = biomeSource.getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(64), QuartPos.fromBlock(z), random.sampler());
        return biome.unwrapKey().map(key -> key.location().equals(ResourceLocation.fromNamespaceAndPath("thebeneath", "lost"))).orElse(false);
    }

    private int top(int x, int z, RandomState random) {
        if (isLost(x, z, random)) return 40;

        double broad = Math.sin(x * 0.008) * Math.cos(z * 0.009) * 15.0;
        double detail = Math.sin(x * 0.026 + z * 0.013) * 4.0;
        double terrain = 43.0 + broad + detail;

        double nearestLostSquared = Double.POSITIVE_INFINITY;
        final int radius = 48;
        final int step = 8;
        for (int dx = -radius; dx <= radius; dx += step) {
            for (int dz = -radius; dz <= radius; dz += step) {
                double squared = (double) dx * dx + (double) dz * dz;
                if (squared >= nearestLostSquared || squared > (double) radius * radius) continue;
                if (isLost(x + dx, z + dz, random)) nearestLostSquared = squared;
            }
        }
        double distance = Math.sqrt(nearestLostSquared);
        double t = Math.min(1.0, distance / radius);
        double blend = t * t * (3.0 - 2.0 * t); // smoothstep
        return Math.max(24, Math.min(67, (int) Math.round(40.0 + (terrain - 40.0) * blend)));
    }

    private BlockState terrainBlock(int y, int height, boolean lost) {
        if (y < 0 || y >= height) return Blocks.AIR.defaultBlockState();
        return lost ? Blocks.BEDROCK.defaultBlockState()
                : (y == height - 1 ? Blocks.BEDROCK.defaultBlockState() : Blocks.BEDROCK.defaultBlockState());
    }

    @Override public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState random, StructureManager structures, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int min = Math.max(0, chunk.getMinBuildHeight());
        for (int lx = 0; lx < 16; lx++) for (int lz = 0; lz < 16; lz++) {
            int x = chunk.getPos().getMinBlockX() + lx, z = chunk.getPos().getMinBlockZ() + lz;
            boolean lost = isLost(x, z, random);
            int height = lost ? 40 : top(x, z, random);
            for (int y = min; y < height; y++) {
                chunk.setBlockState(pos.set(x, y, z), terrainBlock(y, height, lost), false);
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override public int getBaseHeight(int x,int z,Heightmap.Types type,LevelHeightAccessor level,RandomState random) {
        return top(x,z,random);
    }

    @Override public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
        BlockState[] column = new BlockState[level.getHeight()];
        boolean lost = isLost(x, z, random);
        int height = lost ? 40 : top(x, z, random);
        for (int i = 0; i < column.length; i++) {
            column[i] = terrainBlock(level.getMinBuildHeight() + i, height, lost);
        }
        return new NoiseColumn(level.getMinBuildHeight(), column);
    }

    @Override public void buildSurface(WorldGenRegion level,StructureManager structures,RandomState random,ChunkAccess chunk) {}
    @Override public void applyCarvers(WorldGenRegion level,long seed,RandomState random,BiomeManager biomeManager,StructureManager structures,ChunkAccess chunk,GenerationStep.Carving step) {}
    @Override public void spawnOriginalMobs(WorldGenRegion level) {}
    @Override public int getGenDepth() { return 384; }
    @Override public int getMinY() { return -64; }
    @Override public int getSeaLevel() { return 40; }
    @Override public void addDebugScreenInfo(List<String> info,RandomState random,BlockPos pos) { info.add("Beneath natural biome terrain"); }
}
