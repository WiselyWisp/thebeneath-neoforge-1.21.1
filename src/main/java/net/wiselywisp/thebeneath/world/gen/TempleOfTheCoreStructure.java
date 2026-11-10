package net.wiselywisp.thebeneath.world.gen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;


public final class TempleOfTheCoreStructure extends Structure {
    public static final MapCodec<TempleOfTheCoreStructure> CODEC =
            simpleCodec(TempleOfTheCoreStructure::new);

    private static final ResourceKey<StructureTemplatePool> START_POOL = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            ResourceLocation.fromNamespaceAndPath("thebeneath", "temple_of_the_core/start_pool"));

    public TempleOfTheCoreStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();

        Holder<Biome> anchorBiome = context.biomeSource().getNoiseBiome(
                QuartPos.fromBlock(x), QuartPos.fromBlock(64),
                QuartPos.fromBlock(z), context.randomState().sampler());
        if (!context.validBiome().test(anchorBiome)) {
            return Optional.empty();
        }

        int surface = context.chunkGenerator().getFirstFreeHeight(
                x, z, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());

        Holder<StructureTemplatePool> pool = context.registryAccess()
                .registryOrThrow(Registries.TEMPLATE_POOL).getHolderOrThrow(START_POOL);
        return JigsawPlacement.addPieces(context, pool, Optional.empty(), 1,
                new BlockPos(x, surface + 1, z), false, Optional.empty(), 80,
                PoolAliasLookup.EMPTY, DimensionPadding.ZERO,
                LiquidSettings.APPLY_WATERLOGGING);
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.TEMPLE_OF_THE_CORE.get();
    }
}
