package net.wiselywisp.thebeneath.world.gen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;

public final class MyStructureProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS =
            DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR, TheBeneath.MOD_ID);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<ReplaceBedrockProcessor>> REPLACE_BEDROCK =
            PROCESSORS.register("replace_bedrock", () -> () -> ReplaceBedrockProcessor.CODEC);
    private MyStructureProcessors() {}
}
