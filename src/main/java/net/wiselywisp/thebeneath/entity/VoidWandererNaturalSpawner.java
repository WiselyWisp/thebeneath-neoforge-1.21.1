package net.wiselywisp.thebeneath.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.world.dimension.ModDimensions;


public final class VoidWandererNaturalSpawner {
    private static final ResourceKey<Biome> LOST = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "lost"));
    private static final int INTERVAL_TICKS = 240;
    private static final int MAX_NEAR_PLAYER = 4;
    private static final int ATTEMPTS_PER_PLAYER = 16;
    private static final double MAX_DISTANCE = 96.0;
    private static final double MIN_DISTANCE_SQR = 24.0 * 24.0;

    private VoidWandererNaturalSpawner() {}

    public static void tick(MinecraftServer server) {
        ServerLevel level = server.getLevel(ModDimensions.THE_BENEATH_LEVEL_KEY);
        if (level == null || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                || level.getGameTime() % INTERVAL_TICKS != 0L) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || !player.isAlive() || level.random.nextInt(4) != 0) {
                continue;
            }

            if (level.getEntitiesOfClass(VoidWanderer.class,
                    new AABB(player.blockPosition()).inflate(MAX_DISTANCE)).size() >= MAX_NEAR_PLAYER) {
                continue;
            }

            for (int attempt = 0; attempt < ATTEMPTS_PER_PLAYER; attempt++) {
                double angle = level.random.nextDouble() * Math.PI * 2.0;
                double distance = 32.0 + level.random.nextDouble() * 48.0;
                int x = (int) Math.floor(player.getX() + Math.cos(angle) * distance);
                int z = (int) Math.floor(player.getZ() + Math.sin(angle) * distance);
                if (!level.getChunkSource().hasChunk(x >> 4, z >> 4)) {
                    continue;
                }

                for (int y = 90; y >= 25; --y) {
                    BlockPos floor = new BlockPos(x, y, z);
                    BlockPos feet = floor.above();
                    BlockPos head = feet.above();
                    if (!level.getBiome(feet).is(LOST)
                            || !level.getBlockState(floor).isSolid()
                            || !level.getBlockState(feet).isAir()
                            || !level.getBlockState(head).isAir()) {
                        continue;
                    }
                    double sx = x + 0.5;
                    double sy = y + 1.0;
                    double sz = z + 0.5;
                    if (!farFromAllPlayers(level, sx, sy, sz)) {
                        break;
                    }
                    VoidWanderer wanderer = ModEntities.VOID_WANDERER.get().create(level);
                    if (wanderer == null) {
                        return;
                    }
                    wanderer.moveTo(sx, sy, sz, level.random.nextFloat() * 360.0F, 0.0F);
                    if (level.noCollision(wanderer) && wanderer.checkSpawnObstruction(level)) {
                        wanderer.finalizeSpawn(level, level.getCurrentDifficultyAt(feet),
                                MobSpawnType.NATURAL, null);
                        level.addFreshEntity(wanderer);
                        return;
                    }
                    break;
                }
            }
        }
    }

    private static boolean farFromAllPlayers(ServerLevel level, double x, double y, double z) {
        for (ServerPlayer player : level.players()) {
            if (!player.isSpectator() && player.isAlive()
                    && player.distanceToSqr(x, y, z) < MIN_DISTANCE_SQR) {
                return false;
            }
        }
        return true;
    }
}
