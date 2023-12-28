package insane96mcp.iguanatweaksreborn.module.items.itemstats;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Item Stats", description = "Less durable items and efficient tools. Items Durability and Efficiency are controlled via json in this feature's folder. Note that removing entries from the json requires a Minecraft Restart")
@LoadFeature(module = Modules.Ids.ITEMS)
public class ItemStats extends Feature {

	public static final String TOOL_EFFICIENCY_LANG = "iguanatweaksreborn.tool_efficiency";
	public static final String TOOL_DURABILITY_LANG = "iguanatweaksreborn.tool_durability";
	public static final String BROKEN_DURABILITY_LANG = "iguanatweaksreborn.broken_durability";
	public static final String NO_EFFICIENCY_ITEM_LANG = "iguanatweaksreborn.no_efficiency_item";
	public static final String BROKEN_ITEM_LANG = "iguanatweaksreborn.broken_item";
	public static final String NO_DAMAGE_ITEM_LANG = "iguanatweaksreborn.no_damage_item";
	public static final TagKey<Item> NO_DAMAGE = ITRItemTagsProvider.create("no_damage");
	public static final TagKey<Item> NO_EFFICIENCY = ITRItemTagsProvider.create("no_efficiency");
	public static final TagKey<Item> NOT_UNBREAKABLE = ITRItemTagsProvider.create("not_unbreakable");
	public static final TagKey<Item> REMOVE_ORIGINAL_MODIFIERS_TAG = ITRItemTagsProvider.create("remove_original_modifiers");

	@Config
	@Label(name = "More Items Tooltips", description = "If set to true items in the 'no_damage_items' and 'no_efficiency_items' will get a tooltip. Items with durability get a durability tooltip. Tools get an efficiency tooltip.")
	public static Boolean moreItemsTooltips = true;
	@Config
	@Label(name = "Unbreakable Items", description = "If set to true items will no longer break, will be left with 1 durability. Items in the iguanatweaksreborn:not_unbreakable tag will break instead.")
	public static Boolean unbreakableItems = true;
	@Config
	@Label(name = "Unbreakable Enchanted Items", description = "If set to true items will no longer break if enchanted. Ignores the iguanatweaksreborn:not_unbreakable item tag.")
	public static Boolean unbreakableEnchantedItems = true;

	public ItemStats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onAttributeEvent(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;

		for (ItemStatistics itemStats : ItemStatsReloadListener.STATS) {
			itemStats.applyAttributes(event, event.getItemStack(), event.getModifiers());
		}
	}

	@SubscribeEvent
	public void processAttackDamage(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !unbreakableItems
				|| !(event.getSource().getDirectEntity() instanceof Player player))
			return;

		ItemStack stack = player.getMainHandItem();
		if (Utils.isItemInTag(stack.getItem(), NO_DAMAGE)) {
			event.setAmount(1f);
			player.displayClientMessage(Component.translatable(NO_DAMAGE_ITEM_LANG), true);
		}
	}

	public static boolean shouldNotBreak(ItemStack stack) {
		return !Utils.isItemInTag(stack.getItem(), NOT_UNBREAKABLE) || (unbreakableEnchantedItems && stack.isEnchanted());
	}

	public static boolean isBroken(ItemStack stack) {
		return stack.isDamageableItem() && shouldNotBreak(stack) && stack.getDamageValue() >= stack.getMaxDamage() - 1;
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| !unbreakableItems
				|| event.player.level().isClientSide
				|| event.phase == TickEvent.Phase.START
				|| event.player.tickCount % 20 != event.player.getId() % 20)
			return;

		for (ItemStack stack : event.player.getArmorSlots()) {
			if (stack.isEmpty() || !isBroken(stack))
				continue;
			event.player.level().playSound(null, event.player, SoundEvents.ALLAY_HURT, SoundSource.PLAYERS, 0.7f, 2f);
			EquipmentSlot equipmentSlot = Player.getEquipmentSlotForItem(stack);
			if (stack.getItem() instanceof Equipable) {
				event.player.setItemSlot(equipmentSlot, ItemStack.EMPTY);
				if (!event.player.addItem(stack))
					event.player.drop(stack, true);
			}
		}
	}

	@SubscribeEvent
	public void processEfficiencyMultipliers(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();
		if (stack.getMaxDamage() == 0)
			return;
		if (Utils.isItemInTag(stack.getItem(), NO_EFFICIENCY)) {
			event.setCanceled(true);
			event.getEntity().displayClientMessage(Component.translatable(NO_EFFICIENCY_ITEM_LANG), true);
		}
		else if (isBroken(stack)){
			if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
				event.setCanceled(true);
				event.getEntity().displayClientMessage(Component.translatable(BROKEN_ITEM_LANG), true);
			}
		}
	}

	@SubscribeEvent
	public void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled()
				|| !unbreakableItems)
			return;

		ItemStack stack = event.getItemStack();
		if (stack.getMaxDamage() == 0)
			return;
		if (isBroken(stack)) {
			event.setCanceled(true);
			event.getEntity().displayClientMessage(Component.translatable(BROKEN_ITEM_LANG), true);
		}
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if (!this.isEnabled()
				|| !unbreakableItems)
			return;

		ItemStack stack = event.getItemStack();
		if (stack.getMaxDamage() == 0)
			return;
		if (isBroken(stack)) {
			event.setCanceled(true);
			event.getEntity().displayClientMessage(Component.translatable(BROKEN_ITEM_LANG), true);
		}
	}

	@SubscribeEvent
	public void processBrokenToolsOnAttack(LivingAttackEvent event) {
		if (!this.isEnabled()
				|| !unbreakableItems
				|| !(event.getSource().getDirectEntity() instanceof Player player))
			return;

		ItemStack stack = player.getMainHandItem();
		if (stack.getMaxDamage() == 0)
			return;
		if (isBroken(stack)) {
			event.setCanceled(true);
			player.displayClientMessage(Component.translatable(BROKEN_ITEM_LANG), true);
		}
	}

	@SubscribeEvent
	public void processItemDamaging(HurtItemStackEvent event) {
		if (!this.isEnabled()
				|| !unbreakableItems
				|| event.getPlayer() == null)
			return;

		ItemStack stack = event.getStack();
		if (!shouldNotBreak(stack))
			return;
		if (event.getAmount() >= stack.getMaxDamage() - stack.getDamageValue() - 1) {
			event.getStack().setDamageValue(event.getStack().getMaxDamage() - 1);
			event.setAmount(0);
			EquipmentSlot equipmentSlot = Player.getEquipmentSlotForItem(stack);
			if (stack.getItem() instanceof Equipable) {
				event.getPlayer().setItemSlot(equipmentSlot, ItemStack.EMPTY);
				if (!event.getPlayer().addItem(stack))
					event.getPlayer().drop(stack, true);
			}
			event.getPlayer().broadcastBreakEvent(equipmentSlot);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !moreItemsTooltips)
			return;

		ItemStack stack = event.getItemStack();
		if (Utils.isItemInTag(stack.getItem(), NO_DAMAGE)) {
			event.getToolTip().add(Component.translatable(NO_DAMAGE_ITEM_LANG).withStyle(ChatFormatting.RED));
		}
		if (Utils.isItemInTag(stack.getItem(), NO_EFFICIENCY)) {
			event.getToolTip().add(Component.translatable(NO_EFFICIENCY_ITEM_LANG).withStyle(ChatFormatting.RED));
		}
		else if (stack.getItem() instanceof DiggerItem diggerItem){
			int lvl = stack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
			float toolEfficiency = diggerItem.speed;
			float bonusToolEfficiency = EnchantmentsFeature.getEfficiencyBonus(toolEfficiency, lvl);
			if (lvl > 0)
				toolEfficiency += bonusToolEfficiency;
			event.getToolTip().add(CommonComponents.space().append(Component.translatable(TOOL_EFFICIENCY_LANG, IguanaTweaksReborn.ONE_DECIMAL_FORMATTER.format(toolEfficiency))).withStyle(ChatFormatting.DARK_GREEN));
		}

		if (stack.isDamageableItem()) {
			int durabilityLeft = stack.getMaxDamage() - stack.getDamageValue();
			MutableComponent component;
            if (isBroken(stack))
                component = Component.translatable(BROKEN_DURABILITY_LANG).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD);
            else
                component = Component.translatable(TOOL_DURABILITY_LANG, durabilityLeft, stack.getMaxDamage()).withStyle(ChatFormatting.GRAY);
            if (durabilityLeft > 1 && stack.getAllEnchantments().containsKey(Enchantments.UNBREAKING)) {
				int lvl = stack.getAllEnchantments().get(Enchantments.UNBREAKING);
				component.append(Component.literal(" (+%.0f%%)".formatted(getUnbreakingPercentageBonus(lvl) * 100f)).withStyle(ChatFormatting.LIGHT_PURPLE));
			}
			event.getToolTip().add(component);
		}
	}

	private static float getUnbreakingPercentageBonus(int lvl) {
		return 1f / (1f - EnchantmentsFeature.unbreakingBonus(lvl)) - 1f;
	}
}