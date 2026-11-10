package net.wiselywisp.thebeneath.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.wiselywisp.thebeneath.world.dimension.ModDimensions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public final class CeilingExitOpeningHandler {
    private static final Queue<ChunkPos> PENDING = new ConcurrentLinkedQueue<>();
    private CeilingExitOpeningHandler() {}

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level
                && level.dimension().equals(ModDimensions.THE_BENEATH_LEVEL_KEY)) {
            PENDING.add(event.getChunk().getPos());
        }
    }

    public static void processQueuedChunks(MinecraftServer server) {
        ServerLevel level = server.getLevel(ModDimensions.THE_BENEATH_LEVEL_KEY);
        if (level == null) {
            PENDING.clear();
            return;
        }

        for (int i = 0; i < 64; i++) {
            ChunkPos chunk = PENDING.poll();
            if (chunk == null) break;
            if (!level.getChunkSource().hasChunk(chunk.x, chunk.z)) continue;
            int ceilingY = level.getMaxBuildHeight() - 1;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x++) {
                for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z++) {

                    boolean gatewayBelow = false;
                    for (int y = ceilingY - 2; y <= ceilingY - 1; y++) {
                        pos.set(x, y, z);
                        if (level.getBlockState(pos).is(Blocks.END_GATEWAY)) {
                            gatewayBelow = true;
                            break;
                        }
                    }
                    if (gatewayBelow) {
                        pos.set(x, ceilingY, z);
                        if (level.getBlockState(pos).is(Blocks.BEDROCK)) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
}
