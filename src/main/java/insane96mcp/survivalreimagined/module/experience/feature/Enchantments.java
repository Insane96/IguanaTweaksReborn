package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.enchantment.Magnetic;
import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

@Label(name = "Enchantments", description = "Change some enchantments and anvil related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class Enchantments extends Feature {
	@Config
	@Label(name = "Unmending", description = """
			Makes mending reset the repair cost of an item to 0 when applied to it. No longer repairs items with XP.
			If an item has already mending, the enchantment will be removed and repair cost reset when used in an anvil.
			Applying mending still requires the base repair cost of the item (you can't add Mending if the operation is 'Too Expensive'""")
	public static Boolean unmending = true;
	@Config
	@Label(name = "Repaired Tooltip", description = "If true (and Unmending is enabled), items will have a 'Item has been repaired' tooltip.")
	public static Boolean repairedTooltip = false;

	@Config
	@Label(name = "Efficiency changed formula", description = "Change the efficiency formula from tool_efficiency+(lvl*lvl+1) to (tool_efficiency + 75% * level)")
	public static Boolean changeEfficiencyFormula = true;

	//TODO Make enchantments deactivable

	public Enchantments(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onAnvilUse(AnvilUpdateEvent event) {
		if (!this.isEnabled()
				|| !unmending)
			return;

		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();
		ItemStack out = event.getOutput();

		if (out.isEmpty() && (left.isEmpty() || right.isEmpty()))
			return;

		boolean isMended = false;

		Map<Enchantment, Integer> enchLeft = EnchantmentHelper.getEnchantments(left);
		Map<Enchantment, Integer> enchRight = EnchantmentHelper.getEnchantments(right);

		if (enchLeft.containsKey(net.minecraft.world.item.enchantment.Enchantments.MENDING) || enchRight.containsKey(net.minecraft.world.item.enchantment.Enchantments.MENDING)) {
			if (left.getItem() == right.getItem())
				isMended = true;

			if (right.getItem() == Items.ENCHANTED_BOOK)
				isMended = true;
		}

		if (isMended) {
			if (out.isEmpty())
				out = left.copy();

			if (!out.hasTag())
				out.setTag(new CompoundTag());

			Map<Enchantment, Integer> enchOutput = EnchantmentHelper.getEnchantments(out);
			enchOutput.putAll(enchRight);
			enchOutput.remove(net.minecraft.world.item.enchantment.Enchantments.MENDING);

			EnchantmentHelper.setEnchantments(enchOutput, out);

			out.setRepairCost(0);
			if (out.isDamageableItem())
				out.setDamageValue(0);

			event.setOutput(out);
			if (event.getCost() == 0)
				event.setCost(1);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void unmending(PlayerXpEvent.PickupXp event) {
		if (!this.isEnabled()
				|| !unmending
				|| event.isCanceled())
			return;

		Player player = event.getEntity();
		ExperienceOrb orb = event.getOrb();

		player.takeXpDelay = 2;
		player.take(orb, 1);
		if(orb.value > 0)
			player.giveExperiencePoints(orb.value);

		--orb.count;
		if (orb.count == 0)
			orb.discard();
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onEntityTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;

		Magnetic.tryPullItems(event.getEntity());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !unmending
				|| !repairedTooltip)
			return;

		int repairCost = event.getItemStack().getBaseRepairCost();
		if(repairCost > 0)
			event.getToolTip().add(Component.translatable(Strings.Translatable.ITEM_REPAIRED).withStyle(ChatFormatting.YELLOW));
	}
}