package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.MutableInt;

@Label(name = "Unbreaking overhaul", description = "Changes how Unbreaking works.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class UnbreakingOverhaul extends Feature {

	@Config
	@Label(name = "Max I level", description = "Unbreaking max level is set to 1. Also changes the cost and the rarity.")
	public static Boolean maxOneLevel = true;

	@Config
	@Label(name = "Enchanted Item Fragment", description = "Enable Enchanted Item Fragments.")
	public static Boolean enchantedItemFragment = true;

	public static RegistryObject<Item> ITEM_FRAGMENT = SRItems.REGISTRY.register("item_fragment", () -> new EnchantedBookItem(new Item.Properties().stacksTo(1)));

	public UnbreakingOverhaul(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		if (maxOneLevel)
			Enchantments.UNBREAKING.rarity = Enchantment.Rarity.RARE;
		else
			Enchantments.UNBREAKING.rarity = Enchantment.Rarity.UNCOMMON;
	}

	public static boolean isMaxOneLevel() {
		return Feature.isEnabled(EnchantmentsFeature.class) && maxOneLevel;
	}

	@SubscribeEvent
	public void onItemDestroyed(PlayerDestroyItemEvent event) {
		if (!this.isEnabled()
				|| !enchantedItemFragment
				|| !event.getOriginal().isDamageableItem())
			return;

		if (event.getOriginal().getEnchantmentLevel(Enchantments.UNBREAKING) <= 0)
			return;

		ItemStack itemStack = new ItemStack(ITEM_FRAGMENT.get());
		event.getOriginal().getAllEnchantments().forEach((enchantment, lvl) -> {
			EnchantmentInstance instance = new EnchantmentInstance(enchantment, lvl);
			EnchantedBookItem.addEnchantment(itemStack, instance);
		});
		//event.getOriginal().getAllEnchantments().forEach(itemStack::enchant);
		if (!itemStack.hasTag())
			itemStack.setTag(new CompoundTag());
		itemStack.getTag().putString("applicable_to", ForgeRegistries.ITEMS.getKey(event.getOriginal().getItem()).toString());
		ItemEntity itemEntity = new ItemEntity(event.getEntity().level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), itemStack);
		itemEntity.setDefaultPickUpDelay();
		event.getEntity().level.addFreshEntity(itemEntity);
	}

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		if (!this.isEnabled()
				|| !enchantedItemFragment
				|| !event.getRight().is(ITEM_FRAGMENT.get())
				|| !event.getLeft().isEnchantable()
				|| event.getLeft().isEnchanted())
			return;

		ResourceLocation applicableTo = null;
		if (event.getRight().hasTag()) {
			//noinspection DataFlowIssue
			String s = event.getRight().getTag().getString("applicable_to");
			if (!s.isBlank())
				applicableTo = ResourceLocation.tryParse(s);
		}
		if (applicableTo != null) {
			Item item = ForgeRegistries.ITEMS.getValue(applicableTo);
			if (item == null || !event.getLeft().is(item))
				return;
		}
		MutableInt cost = new MutableInt(0);
		ItemStack output = event.getLeft().copy();
		EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(event.getRight())).forEach((enchantment, lvl) -> {
			if (!enchantment.canEnchant(output))
				return;
			switch (enchantment.getRarity()) {
				case COMMON -> cost.add(1 * lvl);
				case UNCOMMON -> cost.add(2 * lvl);
				case RARE -> cost.add(4 * lvl);
				case VERY_RARE -> cost.add(8 * lvl);
			}
			output.enchant(enchantment, lvl);
		});

		event.setCost(cost.getValue());
		event.setOutput(output);
		event.setMaterialCost(1);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		//noinspection DataFlowIssue
		if (!event.getItemStack().is(ITEM_FRAGMENT.get())
				|| !event.getItemStack().hasTag()
				|| !event.getItemStack().getTag().contains("applicable_to"))
			return;

		Item applicableTo = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(event.getItemStack().getTag().getString("applicable_to")));
		if (applicableTo == null)
			return;
		event.getToolTip().add(Component.translatable(SurvivalReimagined.MOD_ID + ".item_fragment.applicable_to").append(applicableTo.getDescription().copy().withStyle(ChatFormatting.AQUA)));
	}
}