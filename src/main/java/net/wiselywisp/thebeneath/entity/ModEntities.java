package net.wiselywisp.thebeneath.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.wiselywisp.thebeneath.TheBeneath;


public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TheBeneath.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<VoidWanderer>> VOID_WANDERER =
            ENTITY_TYPES.register("void_wanderer", () -> EntityType.Builder
                    .of(VoidWanderer::new, MobCategory.AMBIENT)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .build("void_wanderer"));

    public static final DeferredHolder<EntityType<?>, EntityType<VoidRift>> VOID_RIFT =
            ENTITY_TYPES.register("void_rift", () -> EntityType.Builder
                    .of(VoidRift::new, MobCategory.MISC)
                    .sized(2.4F, 2.4F)
                    .clientTrackingRange(10)
                    .build("void_rift"));

    private ModEntities() {}
}
