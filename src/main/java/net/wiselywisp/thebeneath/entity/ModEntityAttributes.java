package net.wiselywisp.thebeneath.entity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.wiselywisp.thebeneath.TheBeneath;

@EventBusSubscriber(modid = TheBeneath.MOD_ID)
public final class ModEntityAttributes {
    @SubscribeEvent
    public static void register(EntityAttributeCreationEvent event) {
        event.put(ModEntities.VOID_WANDERER.get(), VoidWanderer.createAttributes().build());
    }
}
