package net.wiselywisp.thebeneath.world;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.world.gen.feature.CeilingFeature;
import net.wiselywisp.thebeneath.world.gen.feature.VoidFlowerGen;
import net.wiselywisp.thebeneath.world.gen.feature.UnstablePitFeature;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, TheBeneath.MOD_ID);
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> VOID_FLOWER =
            FEATURES.register("void_flower", () -> new VoidFlowerGen(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CEILING =
            FEATURES.register("ceiling", () -> new CeilingFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> UNSTABLE_PIT =
            FEATURES.register("unstable_pit", () -> new UnstablePitFeature(NoneFeatureConfiguration.CODEC));
    private ModFeatures() {}
}
