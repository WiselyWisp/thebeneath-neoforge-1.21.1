package net.wiselywisp.thebeneath.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.world.dimension.ModDimensions;


public final class VoidRiftSpawner {
    private static final ResourceKey<Biome> SHATTERED_HILLS = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "shattered_hills"));
    private static final int INTERVAL = 20 * 30;
    private VoidRiftSpawner() { }

    public static void tick(MinecraftServer server) {
        ServerLevel level = server.getLevel(ModDimensions.THE_BENEATH_LEVEL_KEY);
        if (level == null || level.getGameTime() % INTERVAL != 0
                || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) return;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || !player.isAlive() || level.random.nextInt(4) != 0) continue;
            if (!level.getBiome(player.blockPosition()).is(SHATTERED_HILLS)) continue;
            if (level.getEntitiesOfClass(VoidRift.class,
                    new AABB(player.blockPosition()).inflate(112)).size() >= 2) continue;
            for (int attempt = 0; attempt < 14; attempt++) {
                double a = level.random.nextDouble() * Math.PI * 2;
                int d = 28 + level.random.nextInt(45);
                int x = (int) Math.floor(player.getX() + Math.cos(a) * d);
                int z = (int) Math.floor(player.getZ() + Math.sin(a) * d);
                if (!level.getChunkSource().hasChunk(x >> 4, z >> 4)) continue;
                for (int y = 100; y >= 25; y--) {
                    BlockPos floor = new BlockPos(x, y, z);
                    BlockPos space = floor.above();
                    if (!level.getBiome(space).is(SHATTERED_HILLS)
                            || !level.getBlockState(floor).isSolid()
                            || !level.getBlockState(space).isAir()
                            || !level.getBlockState(space.above()).isAir()
                            || !level.getBlockState(space.above(2)).isAir()) continue;
                    VoidRift rift = ModEntities.VOID_RIFT.get().create(level);
                    if (rift != null) {
                        rift.moveTo(x + 0.5, y + 1.0, z + 0.5, 0, 0);
                        if (level.noCollision(rift.getBoundingBox())) level.addFreshEntity(rift);
                    }
                    return;
                }
            }
        }
    }
}
