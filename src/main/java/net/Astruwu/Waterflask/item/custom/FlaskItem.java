package net.Astruwu.Waterflask.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlaskItem extends Item {

    private static final int MAX_WATER = 250*12;
    // 250mb times 12 bottles
    private static final int WATER_INCREMENT = 250;
    // Amount of water added per use in mb

    public FlaskItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof Player) {
            Player player = (Player) pLivingEntity;
            int waterAmount = getStoredWater(pStack);
            if (waterAmount > 0) {
                setStoredWater(pStack, waterAmount - 250); //250mb is 1 bottle worth of water
                player.getFoodData().eat(2, 0.2F); //water drinking
            }
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
        //drinking animation
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32; //duration of drinking in ticks
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);
        tooltip.add(Component.literal("Water: " + getStoredWater(stack)/250 + "/" + MAX_WATER/250 + " bottles"));
    }

    // get stored water in item NBT
    public int getStoredWater(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt("WaterAmount");
    }

    // set stored water in item NBT
    public void setStoredWater(ItemStack stack, int amount) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("WaterAmount", amount);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true; //bundle esque bar to show amount of water
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getStoredWater(stack) / MAX_WATER); //bar thickness
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x3f76e4; // water color
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
//penis
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // Check if the block is a water block
            if (state.getBlock() == Blocks.WATER) {
                int currentWaterAmount = getStoredWater(stack);
                if (currentWaterAmount < MAX_WATER) {
                    int fillAmount = Math.min(WATER_INCREMENT, MAX_WATER - currentWaterAmount);
                    setStoredWater(stack, currentWaterAmount + fillAmount); // add the amount of water to the flask
                    player.swing(hand);
                    if (!world.isClientSide) {
                        player.displayClientMessage(Component.literal("Filled the flask with water!"), true);
                    }
                    return InteractionResultHolder.success(stack);
                } else {
                    // displays message to player that the flask is full
                    if (!world.isClientSide) {
                        player.displayClientMessage(Component.literal("The flask is full!"), true);
                    }
                    return InteractionResultHolder.fail(stack);
                }
            }
        }

        // If not looking at water, handle drinking
        if (getStoredWater(stack) > 0) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        } else {
            // Optionally, you can add a message to the player indicating the flask is empty
            if (!world.isClientSide) {
                player.displayClientMessage(Component.literal("The flask is empty!"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
    }
}



