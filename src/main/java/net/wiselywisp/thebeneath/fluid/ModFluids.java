package net.wiselywisp.thebeneath.fluid;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.block.ModBlocks;
import net.wiselywisp.thebeneath.item.ModItems;

public final class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, TheBeneath.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, TheBeneath.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> ESSENCE_TYPE = FLUID_TYPES.register(
            "essence_of_the_void", () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.thebeneath.essence_of_the_void")
                    .density(1200).viscosity(1500).lightLevel(5)
                    .canSwim(false).canDrown(false)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    // Suppliers avoid accessing these fields before registration is complete.
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> ESSENCE = FLUIDS.register(
            "essence_of_the_void", () -> new BaseFlowingFluid.Source(essenceProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_ESSENCE = FLUIDS.register(
            "flowing_essence_of_the_void", () -> new BaseFlowingFluid.Flowing(essenceProperties()));

    private static BaseFlowingFluid.Properties essenceProperties() {
        return new BaseFlowingFluid.Properties(ESSENCE_TYPE, ESSENCE, FLOWING_ESSENCE)
                .block(ModBlocks.ESSENCE_OF_THE_VOID)
                .bucket(ModItems.ESSENCE_BUCKET)
                .slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(15);
    }
    private ModFluids() {}
}
