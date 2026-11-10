package net.wiselywisp.thebeneath.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;


public final class VoidRift extends Entity {
    private static final int LIFETIME = 20 * 60 * 10; // 10 minutes
    private int age;

    public VoidRift(EntityType<? extends VoidRift> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) { age = tag.getInt("RiftAge"); }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("RiftAge", age); }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (++age >= LIFETIME) { discard(); return; }
        if (!(level() instanceof ServerLevel serverLevel)) return;
        AABB area = getBoundingBox().inflate(0.18);
        for (ServerPlayer player : serverLevel.getEntitiesOfClass(ServerPlayer.class, area,
                p -> p.isAlive() && !p.isSpectator())) {
            if (player.getBoundingBox().intersects(area) && VoidRiftTravel.tryTravel(player)) {
                discard();
                break;
            }
        }
    }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 160.0 * 160.0; }
}
