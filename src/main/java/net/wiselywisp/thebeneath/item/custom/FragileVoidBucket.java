package net.wiselywisp.thebeneath.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class FragileVoidBucket extends BucketItem {
    public FragileVoidBucket(Fluid fluid, Properties properties) { super(fluid, properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        if (!level.isClientSide && result.getResult().consumesAction() && !player.getAbilities().instabuild
                && result.getObject().is(Items.BUCKET)) {
            player.setItemInHand(hand, ItemStack.EMPTY);
            return InteractionResultHolder.sidedSuccess(ItemStack.EMPTY, false);
        }
        return result;
    }
}
