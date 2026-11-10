package net.wiselywisp.thebeneath.entity;

import java.util.EnumSet;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.sounds.SoundSource;
import net.wiselywisp.thebeneath.sound.ModSounds;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;


public class VoidWanderer extends PathfinderMob {
    private static final float THORNS_DAMAGE = 6.0F;

    private static final float ENCOUNTER_DAMAGE = 10.0F;
    private static final float RANGED_TELEPORT_CHANCE = 0.35F;
    private static final int TELEPORT_ATTEMPTS = 32;
    private static final double TELEPORT_MIN_DISTANCE = 12.0D;
    private static final double TELEPORT_EXTRA_DISTANCE = 12.0D;


    private static final double DETECTION_DISTANCE = 96.0D;
    private static final double DETECTION_DISTANCE_SQR = DETECTION_DISTANCE * DETECTION_DISTANCE;
    private static final double ENCOUNTER_DISTANCE = 2.0D;
    private static final double ENCOUNTER_DISTANCE_SQR = ENCOUNTER_DISTANCE * ENCOUNTER_DISTANCE;


    private static final double STALK_MOVEMENT_SPEED = 0.13D;
    private static final double SPEED_INCREASE_PER_HIT = 0.04D;
    private static final double MAX_STALK_MOVEMENT_SPEED = 0.29D;
    private static final int ENCOUNTER_COOLDOWN_TICKS = 60;


    private static final int OBSERVATION_MIN_INTERVAL = 160;
    private static final int OBSERVATION_INTERVAL_VARIATION = 120;
    private static final int OBSERVATION_MIN_DURATION = 35;
    private static final int OBSERVATION_DURATION_VARIATION = 35;
    private int observationTicks;
    private int observationDelay = OBSERVATION_MIN_INTERVAL;
    private int ambientSoundDelay = 100;
    private int encounterCooldown;

    public VoidWanderer(EntityType<? extends VoidWanderer> type, Level level) {
        super(type, level);
        this.xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, STALK_MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, DETECTION_DISTANCE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StalkPlayerGoal());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.encounterCooldown > 0) --this.encounterCooldown;

            if (--this.ambientSoundDelay <= 0) {
                this.ambientSoundDelay = 120 + this.random.nextInt(160);
                if (this.findNearestPlayer() != null) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            ModSounds.WANDERER_STATIC.get(), SoundSource.HOSTILE,
                            0.34F, 0.82F + this.random.nextFloat() * 0.32F);
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (!damaged || this.level().isClientSide || source.is(DamageTypes.THORNS)) {
            return damaged;
        }

        if (source.getEntity() instanceof LivingEntity attacker && attacker != this
                && attacker.isAlive()) {
            attacker.hurt(this.damageSources().thorns(this), THORNS_DAMAGE);
        }


        if (this.isAlive() && (source.is(DamageTypeTags.IS_PROJECTILE)
                || source.getDirectEntity() instanceof Projectile)
                && this.random.nextFloat() < RANGED_TELEPORT_CHANCE) {
            LivingEntity attacker = source.getEntity() instanceof LivingEntity living ? living : null;
            teleportAwayFrom(attacker);
        }
        return damaged;
    }

    private void increaseStalkingSpeed() {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(Math.min(MAX_STALK_MOVEMENT_SPEED,
                    speed.getBaseValue() + SPEED_INCREASE_PER_HIT));
        }
    }


    private boolean teleportAwayFrom(LivingEntity target) {
        double awayAngle;
        if (target != null) {
            awayAngle = Math.atan2(this.getZ() - target.getZ(), this.getX() - target.getX());
        } else {
            awayAngle = this.random.nextDouble() * Math.PI * 2.0D;
        }

        for (int attempt = 0; attempt < TELEPORT_ATTEMPTS; attempt++) {

            double spread = attempt < TELEPORT_ATTEMPTS / 2 ? 1.2D : Math.PI;
            double angle = awayAngle + (this.random.nextDouble() * 2.0D - 1.0D) * spread;
            double distance = TELEPORT_MIN_DISTANCE
                    + this.random.nextDouble() * TELEPORT_EXTRA_DISTANCE;
            double x = this.getX() + Math.cos(angle) * distance;
            double z = this.getZ() + Math.sin(angle) * distance;
            double y = this.getY() + this.random.nextInt(9) - 4;

            if (target != null && target.distanceToSqr(x, y, z) < 10.0D * 10.0D) {
                continue;
            }

            if (this.randomTeleport(x, y, z, false)) {
                this.getNavigation().stop();
                this.setDeltaMovement(0.0D, 0.0D, 0.0D);
                return true;
            }
        }
        return false;
    }

    private boolean canStalk(Player player) {
        return player != null && player.isAlive()
                && !player.isSpectator()
                && this.distanceToSqr(player) <= DETECTION_DISTANCE_SQR;
    }

    private Player findNearestPlayer() {
        Player nearest = null;
        double nearestDistanceSqr = DETECTION_DISTANCE_SQR;

        for (Player player : this.level().players()) {
            if (canStalk(player)) {
                double distanceSqr = this.distanceToSqr(player);
                if (distanceSqr <= nearestDistanceSqr) {
                    nearestDistanceSqr = distanceSqr;
                    nearest = player;
                }
            }
        }
        return nearest;
    }

    private final class StalkPlayerGoal extends Goal {
        private Player player;
        private int repathDelay;
        private int stalledTicks;
        private double previousX;
        private double previousZ;

        private StalkPlayerGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (VoidWanderer.this.isPassenger()) {
                return false;
            }
            this.player = VoidWanderer.this.findNearestPlayer();
            return this.player != null;
        }

        @Override
        public boolean canContinueToUse() {
            return !VoidWanderer.this.isPassenger() && VoidWanderer.this.canStalk(this.player);
        }

        @Override
        public void start() {
            this.repathDelay = 0;
            this.stalledTicks = 0;
            this.previousX = VoidWanderer.this.getX();
            this.previousZ = VoidWanderer.this.getZ();

            VoidWanderer.this.observationDelay = OBSERVATION_MIN_INTERVAL
                    + VoidWanderer.this.random.nextInt(OBSERVATION_INTERVAL_VARIATION);
            VoidWanderer.this.observationTicks = 0;
        }

        @Override
        public void stop() {
            VoidWanderer.this.getNavigation().stop();
            VoidWanderer.this.observationTicks = 0;
            this.player = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.player == null) {
                return;
            }

            VoidWanderer.this.getLookControl().setLookAt(this.player, 30.0F, 30.0F);


            if (VoidWanderer.this.observationTicks > 0) {
                --VoidWanderer.this.observationTicks;
                VoidWanderer.this.getNavigation().stop();
                VoidWanderer.this.getMoveControl().setWantedPosition(
                        VoidWanderer.this.getX(), VoidWanderer.this.getY(), VoidWanderer.this.getZ(), 0.0D);
                if (VoidWanderer.this.distanceToSqr(this.player) > ENCOUNTER_DISTANCE_SQR
                        && VoidWanderer.this.getSensing().hasLineOfSight(this.player)) {
                    this.repathDelay = 0;
                    return;
                }
                VoidWanderer.this.observationTicks = 0;
            }
            if (--VoidWanderer.this.observationDelay <= 0) {
                VoidWanderer.this.observationDelay = OBSERVATION_MIN_INTERVAL
                        + VoidWanderer.this.random.nextInt(OBSERVATION_INTERVAL_VARIATION);
                double distanceSqr = VoidWanderer.this.distanceToSqr(this.player);
                if (distanceSqr > 36.0D && distanceSqr < 625.0D
                        && VoidWanderer.this.getSensing().hasLineOfSight(this.player)
                        && VoidWanderer.this.random.nextFloat() < 0.65F) {
                    VoidWanderer.this.observationTicks = OBSERVATION_MIN_DURATION
                            + VoidWanderer.this.random.nextInt(OBSERVATION_DURATION_VARIATION);
                    VoidWanderer.this.getNavigation().stop();
                    return;
                }
            }


            if (VoidWanderer.this.encounterCooldown == 0
                    && VoidWanderer.this.distanceToSqr(this.player) <= ENCOUNTER_DISTANCE_SQR
                    && VoidWanderer.this.getSensing().hasLineOfSight(this.player)) {
                VoidWanderer.this.encounterCooldown = ENCOUNTER_COOLDOWN_TICKS;

                this.player.hurt(VoidWanderer.this.damageSources().mobAttack(VoidWanderer.this),
                        ENCOUNTER_DAMAGE);
                VoidWanderer.this.increaseStalkingSpeed();
                VoidWanderer.this.teleportAwayFrom(this.player);
                this.repathDelay = 0;
                this.stalledTicks = 0;
                return;
            }


            if (--this.repathDelay <= 0) {
                this.repathDelay = 10 + VoidWanderer.this.random.nextInt(10);
                VoidWanderer.this.getNavigation().moveTo(this.player, 1.0D);
            }

            double movedX = VoidWanderer.this.getX() - this.previousX;
            double movedZ = VoidWanderer.this.getZ() - this.previousZ;
            this.previousX = VoidWanderer.this.getX();
            this.previousZ = VoidWanderer.this.getZ();


            boolean needsToApproach = VoidWanderer.this.distanceToSqr(this.player)
                    > ENCOUNTER_DISTANCE_SQR;
            if (needsToApproach && movedX * movedX + movedZ * movedZ < 0.0004D) {
                ++this.stalledTicks;
            } else {
                this.stalledTicks = 0;
            }
            if (needsToApproach && (VoidWanderer.this.getNavigation().isDone()
                    || this.stalledTicks >= 12)
                    && VoidWanderer.this.getSensing().hasLineOfSight(this.player)) {
                VoidWanderer.this.getMoveControl().setWantedPosition(
                        this.player.getX(), this.player.getY(), this.player.getZ(), 1.0D);
            }
        }
    }
}
