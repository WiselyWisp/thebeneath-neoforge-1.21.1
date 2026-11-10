package net.wiselywisp.thebeneath.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.world.dimension.ModDimensions;


public final class ModSpawnPlacements {
    private static final ResourceKey<Biome> LOST_BIOME = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "lost"));

    private ModSpawnPlacements() {}

    public static void register(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.VOID_WANDERER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) ->
                        level.getLevel().dimension().equals(ModDimensions.THE_BENEATH_LEVEL_KEY)
                        && level.getBiome(pos).is(LOST_BIOME)
                        && Mob.checkMobSpawnRules(type, level, reason, pos, random),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
