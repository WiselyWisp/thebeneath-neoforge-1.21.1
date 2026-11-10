package net.wiselywisp.thebeneath.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.world.dimension.ModDimensions;


public final class VoidRiftTravel {
    private static final Map<MinecraftServer, Map<UUID, Long>> COOLDOWNS = new WeakHashMap<>();
    private static final long COOLDOWN_TICKS = 100L;
    private static final int COLUMNS_PER_DIMENSION = 32;
    private VoidRiftTravel() { }

    public static boolean tryTravel(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null || !player.isAlive() || player.isPassenger()) return false;
        ServerLevel origin = player.serverLevel();
        long now = server.overworld().getGameTime();
        Map<UUID, Long> cooldown = COOLDOWNS.computeIfAbsent(server, ignored -> new java.util.HashMap<>());
        if (cooldown.getOrDefault(player.getUUID(), Long.MIN_VALUE) > now) return false;

        List<ServerLevel> destinations = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            if (level != origin) destinations.add(level);
        }
        if (destinations.isEmpty()) return false;
        Collections.shuffle(destinations, new java.util.Random(origin.random.nextLong()));
        for (ServerLevel target : destinations) {
            BlockPos destination = findSafeDestination(target, player);
            if (destination == null) {
                TheBeneath.LOGGER.debug("[Void Rift] No safe landing found in {} for {}", target.dimension().location(), player.getScoreboardName());
                continue;
            }
            player.stopRiding();
            boolean teleported = player.teleportTo(target, destination.getX() + 0.5,
                    destination.getY(), destination.getZ() + 0.5,
                    Set.of(), player.getYRot(), 0.0F);
            if (teleported) {
                player.setDeltaMovement(0.0, 0.0, 0.0);
                player.resetFallDistance();
                cooldown.put(player.getUUID(), now + COOLDOWN_TICKS);
                TheBeneath.LOGGER.info("[Void Rift] Sent {} from {} to {} at {}", player.getScoreboardName(),
                        origin.dimension().location(), target.dimension().location(), destination);
                return true;
            }
        }
        TheBeneath.LOGGER.debug("[Void Rift] No safe destination found for {}", player.getScoreboardName());
        return false;
    }

    private static BlockPos findSafeDestination(ServerLevel level, ServerPlayer player) {
        var random = level.random;
        var spawn = level.getSharedSpawnPos();
        for (int attempt = 0; attempt < COLUMNS_PER_DIMENSION; attempt++) {
            int radius = 96 + random.nextInt(1400);
            double angle = random.nextDouble() * Math.PI * 2.0;
            int x = spawn.getX() + (int)(Math.cos(angle) * radius);
            int z = spawn.getZ() + (int)(Math.sin(angle) * radius);
            BlockPos probe = new BlockPos(x, 64, z);
            if (!level.getWorldBorder().isWithinBounds(probe)) continue;
            level.getChunkAt(probe);
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            int upper = Math.min(top, level.getMaxBuildHeight() - 4);
            int lower = Math.max(level.getMinBuildHeight() + 3, 12);
            if (level.dimension() == net.minecraft.world.level.Level.NETHER) upper = Math.min(upper, 118);
            if (level.dimension() == ModDimensions.THE_BENEATH_LEVEL_KEY) upper = Math.min(upper, 120);
            for (int y = upper; y >= lower; y--) {
                BlockPos feet = new BlockPos(x, y, z);
                if (isSafe(level, feet, player)) return feet;
            }
        }
        return null;
    }

    private static boolean isSafe(ServerLevel level, BlockPos feet, ServerPlayer player) {
        BlockPos floor = feet.below();
        BlockState beneath = level.getBlockState(floor);
        BlockState atFeet = level.getBlockState(feet);
        BlockState atHead = level.getBlockState(feet.above());
        if (!level.getWorldBorder().isWithinBounds(feet)
                || beneath.isAir() || !beneath.getFluidState().isEmpty()
                || !beneath.isFaceSturdy(level, floor, Direction.UP)
                || !atFeet.isAir() || !atHead.isAir()
                || !atFeet.getFluidState().isEmpty() || !atHead.getFluidState().isEmpty()) return false;

        boolean beneathInteriorBedrock = level.dimension().equals(ModDimensions.THE_BENEATH_LEVEL_KEY)
                && feet.getY() <= 120;
        if (beneath.is(Blocks.MAGMA_BLOCK) || beneath.is(Blocks.CACTUS)
                || beneath.is(Blocks.CAMPFIRE) || beneath.is(Blocks.SOUL_CAMPFIRE)
                || beneath.is(Blocks.POWDER_SNOW)
                || (beneath.is(Blocks.BEDROCK) && !beneathInteriorBedrock)) return false;
        double x = feet.getX() + 0.5;
        double z = feet.getZ() + 0.5;
        double halfWidth = player.getBbWidth() * 0.5;
        AABB box = new AABB(x - halfWidth, feet.getY(), z - halfWidth,
                x + halfWidth, feet.getY() + player.getBbHeight(), z + halfWidth);
        return level.noCollision(box);
    }
}
