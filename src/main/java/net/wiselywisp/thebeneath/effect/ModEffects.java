package net.wiselywisp.thebeneath.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, TheBeneath.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> VOID_CORRUPTION = EFFECTS.register(
            "void_corruption", () -> new VoidCorruptionEffect());

    private static final class VoidCorruptionEffect extends MobEffect {
        private VoidCorruptionEffect() { super(MobEffectCategory.HARMFUL, 0x130820); }
    }
    private ModEffects() {}
}
