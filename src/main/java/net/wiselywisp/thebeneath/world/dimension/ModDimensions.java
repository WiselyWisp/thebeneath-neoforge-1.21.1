package net.wiselywisp.thebeneath.world.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.wiselywisp.thebeneath.TheBeneath;


public final class ModDimensions {
    public static final ResourceKey<LevelStem> THE_BENEATH_KEY =
            ResourceKey.create(Registries.LEVEL_STEM,
                    ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "the_beneath"));
    public static final ResourceKey<Level> THE_BENEATH_LEVEL_KEY =
            ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "the_beneath"));
    public static final ResourceKey<DimensionType> THE_BENEATH_DIM_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "the_beneath_type"));
    private ModDimensions() {}
}
