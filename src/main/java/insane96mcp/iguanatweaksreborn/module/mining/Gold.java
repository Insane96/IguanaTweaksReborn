package insane96mcp.iguanatweaksreborn.module.mining;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Lucky Gold", description = "Makes gold tools more useful")
@LoadFeature(module = Modules.Ids.MINING)
public class Gold extends Feature {

	public static final String LUCKY_GOLD_TOOLTIP = IguanaTweaksReborn.MOD_ID + ".innate_luck";

	@Config
	@Label(name = "Lucky Gold", description = "Changes Gold tools to have an innate Fortune/Looting I and changes the harvest level to be like iron tools.")
	public static Boolean luckyGold = true;


	public Gold(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onLootingCheck(LootingLevelEvent event) {
		if (!this.isEnabled()
				|| !luckyGold
				|| event.getDamageSource() == null
				|| !(event.getDamageSource().getEntity() instanceof LivingEntity livingEntity))
			return;

		ItemStack stack = livingEntity.getMainHandItem();
		if (stack.getItem() instanceof SwordItem swordItem && swordItem.getTier() == Tiers.GOLD) {
			if (event.getLootingLevel() < 1)
				event.setLootingLevel(1);
		}
	}

	public static int getFortuneLevel(int prev, ItemStack itemStack) {
		if(!isEnabled(Gold.class)
				|| !luckyGold
				|| !(itemStack.getItem() instanceof TieredItem tieredItem)
				|| tieredItem.getTier() != Tiers.GOLD
				|| prev >= 1)
			return prev;

		return 1;
	}

	public static Tier getEffectiveTier(Tier originalTier) {
		if(!isEnabled(Gold.class)
				|| !luckyGold
				|| originalTier != Tiers.GOLD)
			return originalTier;

		return Tiers.IRON;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !luckyGold
				|| !(event.getItemStack().getItem() instanceof TieredItem tieredItem)
				|| tieredItem.getTier() != Tiers.GOLD
				|| event.getItemStack().getEnchantmentLevel(Enchantments.BLOCK_FORTUNE) > 0
				|| event.getItemStack().getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0
				|| event.getItemStack().getEnchantmentLevel(Enchantments.MOB_LOOTING) > 0)
			return;

		event.getToolTip().add(Component.empty());
		event.getToolTip().add(Component.translatable(LUCKY_GOLD_TOOLTIP).withStyle(ChatFormatting.GOLD));
	}
}
