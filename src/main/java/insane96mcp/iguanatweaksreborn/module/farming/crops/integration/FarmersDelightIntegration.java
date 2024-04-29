package insane96mcp.iguanatweaksreborn.module.farming.crops.integration;

import insane96mcp.iguanatweaksreborn.module.farming.crops.Crops;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.healthregen.HealthRegen;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModEffects;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nullable;

public class FarmersDelightIntegration {
    public static Block getOnion() {
        return ModBlocks.ONION_CROP.get();
    }
    public static Block getRice() {
        return ModBlocks.RICE_CROP.get();
    }

    public static boolean preventPlanting(ItemStack stack) {
        return stack.is(ModItems.ONION.get()) || stack.is(ModItems.RICE.get());
    }

    public static void tryAlertRice(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().is(Crops.RICE_SEEDS.get()))
            return;
        Player player = event.getEntity();
        BlockState targetState = event.getLevel().getBlockState(event.getPos());
        if (player != null && event.getLevel().getFluidState(event.getPos().relative(event.getHitVec().getDirection())).isEmpty() && event.getHitVec().getDirection() == Direction.UP && (targetState.is(BlockTags.DIRT) || targetState.getBlock() instanceof FarmBlock)) {
            player.displayClientMessage(TextUtils.getTranslation("block.rice.invalid_placement"), true);
        }
    }

    public static boolean preventRichSoilFarmland(BlockState state) {
        return state.is(ModBlocks.RICH_SOIL.get());
    }

    public static void onTick(TickEvent.PlayerTickEvent event) {
        MobEffectInstance effectInstance = event.player.getEffect(ModEffects.NOURISHMENT.get());
        if (effectInstance == null)
            return;

        //noinspection ConstantConditions; Checking with hasEffect
        event.player.addEffect(new MobEffectInstance(HealthRegen.VIGOUR.get(), effectInstance.getDuration() + 1, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon()));
        event.player.removeEffect(ModEffects.NOURISHMENT.get());
    }

    public static float tryApplyComfort(Player player, float amount) {
        if (player.hasEffect(ModEffects.COMFORT.get()))
            amount = amount * 1.2f;
        return amount;
    }

    @Nullable
    public static ItemStack tryPickBlock(Block block) {
        if (block == ModBlocks.RICE_CROP.get() || block == ModBlocks.RICE_CROP_PANICLES.get())
            return new ItemStack(Crops.RICE_SEEDS.get());
        else if (block == ModBlocks.ONION_CROP.get())
            return new ItemStack(Crops.ROOTED_ONION.get());
        return null;
    }
}
