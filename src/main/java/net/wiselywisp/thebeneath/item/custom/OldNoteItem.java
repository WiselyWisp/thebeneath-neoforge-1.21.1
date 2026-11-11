package net.wiselywisp.thebeneath.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.wiselywisp.thebeneath.client.OldNoteClient;

public class OldNoteItem extends Item {
    public OldNoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);


        if (level.isClientSide) {
            OldNoteClient.openScreen();
        }

        return InteractionResultHolder.sidedSuccess(
                stack, level.isClientSide
        );
    }
}