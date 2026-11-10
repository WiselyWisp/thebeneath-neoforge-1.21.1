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

public final class AncientDigSiteStructure extends Structure {
    public static final MapCodec<AncientDigSiteStructure> CODEC =
            simpleCodec(AncientDigSiteStructure::new);

    private static final ResourceKey<StructureTemplatePool> START_POOL = ResourceKey.create(
            Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("thebeneath", "ancient_dig_site"));

    public AncientDigSiteStructure(StructureSettings settings) { super(settings); }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMiddleBlockX();
        int z = context.chunkPos().getMiddleBlockZ();
        int top = context.chunkGenerator().getFirstFreeHeight(x, z,
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        Holder<Biome> surfaceBiome = context.biomeSource().getNoiseBiome(
                QuartPos.fromBlock(x), QuartPos.fromBlock(Math.max(top - 1, 0)),
                QuartPos.fromBlock(z), context.randomState().sampler());
        boolean validSurface = context.validBiome().test(surfaceBiome);
        if (!validSurface) return Optional.empty();

        GenerationContext undergroundContext = new GenerationContext(
                context.registryAccess(), context.chunkGenerator(), context.biomeSource(),
                context.randomState(), context.structureTemplateManager(), context.random(),
                context.seed(), context.chunkPos(), context.heightAccessor(), biome -> true);
        Holder<StructureTemplatePool> pool = context.registryAccess()
                .registryOrThrow(Registries.TEMPLATE_POOL).getHolderOrThrow(START_POOL);
        var result = JigsawPlacement.addPieces(undergroundContext, pool, Optional.empty(), 20,
                new BlockPos(x, -62, z), false, Optional.empty(), 80,
                PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING);
        if (result.isEmpty()) return Optional.empty();


        return Optional.of(new GenerationStub(
                new BlockPos(x, Math.max(top - 1, 0), z),
                result.get().generator()));
    }

    @Override
    public StructureType<?> type() { return ModStructureTypes.ANCIENT_DIG_SITE.get(); }
}
