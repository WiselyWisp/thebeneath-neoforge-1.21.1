package net.wiselywisp.thebeneath.world.gen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;

public final class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, TheBeneath.MOD_ID);
    public static final DeferredHolder<StructureType<?>, StructureType<AncientDigSiteStructure>> ANCIENT_DIG_SITE =
            TYPES.register("ancient_dig_site", () -> () -> AncientDigSiteStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<TempleOfTheCoreStructure>> TEMPLE_OF_THE_CORE =
            TYPES.register("temple_of_the_core", () -> () -> TempleOfTheCoreStructure.CODEC);
    private ModStructureTypes() {}
}
