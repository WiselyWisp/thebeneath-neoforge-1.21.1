package net.wiselywisp.thebeneath.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = "thebeneath")
public class ModCreativeTabEvents {

    @SubscribeEvent
    public static void addItems(BuildCreativeModeTabContentsEvent event) {

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.insertAfter(
                    Items.MUSIC_DISC_PIGSTEP.getDefaultInstance(),
                    ModItems.WOZZY_DISC.get().getDefaultInstance(),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }
    }
}