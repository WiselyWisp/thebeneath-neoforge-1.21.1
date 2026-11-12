package net.wiselywisp.thebeneath.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.util.RandomSource;
import net.wiselywisp.thebeneath.block.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class UnstablePitFeature
        extends Feature<NoneFeatureConfiguration> {

    private static final ResourceKey<Biome> UNSTABLE_PLAINS =
            ResourceKey.create(
                    Registries.BIOME,
                    ResourceLocation.fromNamespaceAndPath(
                            "thebeneath", "unstable_plains"
                    )
            );

    public UnstablePitFeature(
            Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(
            FeaturePlaceContext<NoneFeatureConfiguration> context) {

        WorldGenLevel world = context.level();
        RandomSource random = context.random();

        if (context.origin().getY() != 40) {
            return false;
        }

        int surfaceY = 39;


        int radiusX = 2 + random.nextInt(3);
        int radiusZ = 2 + random.nextInt(3);


        int centralDepth = 6 + random.nextInt(3);
        double phaseX = random.nextDouble() * Math.PI * 2.0;
        double phaseZ = random.nextDouble() * Math.PI * 2.0;

        BlockPos center = context.origin().atY(surfaceY);


        List<BlockPos> footprint = new ArrayList<>();

        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {


                float nx = (float) dx / radiusX;
                float nz = (float) dz / radiusZ;

                float distance = nx * nx + nz * nz;


                float edgeNoise =
                        0.82F + random.nextFloat() * 0.40F;


                if (distance <= edgeNoise) {
                    footprint.add(center.offset(dx, 0, dz));
                }
            }
        }


        if (footprint.size() < 5) {
            return false;
        }
        Map<BlockPos, Integer> columnDepths = new HashMap<>();

        for (BlockPos roof : footprint) {

            int dx = roof.getX() - center.getX();
            int dz = roof.getZ() - center.getZ();


            double nx = (double) dx / radiusX;
            double nz = (double) dz / radiusZ;

            double distance = Math.sqrt(nx * nx + nz * nz);


            int edgeReduction = (int) Math.round(distance * 2.0);


            double noise =
                    Math.sin(dx * 0.9 + phaseX) * 0.85
                            + Math.cos(dz * 0.9 + phaseZ) * 0.85;

            int variation = (int) Math.round(noise);

            int depth = centralDepth - edgeReduction + variation;


            depth = Math.max(2, Math.min(8, depth));

            columnDepths.put(roof, depth);
        }

        for (BlockPos roof : footprint) {

            int depth = columnDepths.get(roof);


            for (int d = 1; d < depth; d++) {
                world.setBlock(
                        roof.below(d),
                        Blocks.AIR.defaultBlockState(),
                        Block.UPDATE_CLIENTS
                );
            }


            world.setBlock(
                    roof,
                    ModBlocks.BRITTLE_BEDROCK.get()
                            .defaultBlockState(),
                    Block.UPDATE_CLIENTS
            );
        }

        return true;

    }
}