package insane96mcp.survivalreimagined.module.items.itemstats;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.data.IdTagValue;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Label(name = "Item Stats", description = "Less durable items and efficient tools. Items Durability and Efficiency are controlled via json in this feature's folder. Note that removing entries from the json requires a Minecraft Restart")
@LoadFeature(module = Modules.Ids.ITEMS)
public class ItemStats extends JsonFeature {

	public static final String TOOL_EFFICIENCY_LANG = "survivalreimagined.tool_efficiency";
	public static final String TOOL_DURABILITY_LANG = "survivalreimagined.tool_durability";
	public static final String BROKEN_DURABILITY_LANG = "survivalreimagined.broken_durability";
	public static final TagKey<Item> NO_DAMAGE = SRItemTagsProvider.create("no_damage");
	public static final TagKey<Item> NO_EFFICIENCY = SRItemTagsProvider.create("no_efficiency");
	public static final TagKey<Item> NOT_UNBREAKABLE = SRItemTagsProvider.create("not_unbreakable");

	public static final RegistryObject<RecipeType<RepairItemRecipe>> REPAIR_ITEM_RECIPE_TYPE = SRRegistries.RECIPE_TYPES.register("repair_item", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "repair_item";
		}
	});
	public static final RegistryObject<RepairItemRecipeSerializer> REPAIR_ITEM_RECIPE_SERIALIZER = SRRegistries.RECIPE_SERIALIZERS.register("repair_item", RepairItemRecipeSerializer::new);

	public static final ArrayList<IdTagValue> ITEM_DURABILITIES_DEFAULT = new ArrayList<>(List.of(
			IdTagValue.newTag("survivalreimagined:equipment/hand/wooden", 127),
			IdTagValue.newTag("survivalreimagined:equipment/hand/stone", 84),
			IdTagValue.newTag("survivalreimagined:equipment/hand/flint", 58),
			IdTagValue.newTag("survivalreimagined:equipment/hand/copper", 42),
			IdTagValue.newTag("survivalreimagined:equipment/hand/golden", 72),
			IdTagValue.newTag("survivalreimagined:equipment/hand/iron", 375),
			IdTagValue.newTag("survivalreimagined:equipment/hand/solarium", 259),
			IdTagValue.newTag("survivalreimagined:equipment/hand/durium", 855),
			IdTagValue.newTag("survivalreimagined:equipment/hand/coated_copper", 260),
			IdTagValue.newTag("survivalreimagined:equipment/hand/keego", 1400),
			IdTagValue.newTag("survivalreimagined:equipment/hand/diamond", 2341),
			IdTagValue.newTag("survivalreimagined:equipment/hand/soul_steel", 3534),
			IdTagValue.newTag("survivalreimagined:equipment/hand/netherite", 2131),

			IdTagValue.newId("minecraft:elytra", 144),
			IdTagValue.newId("minecraft:carrot_on_a_stick", 63),
			IdTagValue.newId("minecraft:fishing_rod", 33),
			IdTagValue.newId("minecraft:shears", 87),
			IdTagValue.newId("minecraft:trident", 475),

			IdTagValue.newId("minecraft:leather_helmet", 72),
			IdTagValue.newId("minecraft:leather_chestplate", 96),
			IdTagValue.newId("minecraft:leather_leggings", 90),
			IdTagValue.newId("minecraft:leather_boots", 78),

			IdTagValue.newId("survivalreimagined:chained_copper_helmet", 48),
			IdTagValue.newId("survivalreimagined:chained_copper_chestplate", 64),
			IdTagValue.newId("survivalreimagined:chained_copper_leggings", 60),
			IdTagValue.newId("survivalreimagined:chained_copper_boots", 52),

			IdTagValue.newId("minecraft:chainmail_helmet", 48),
			IdTagValue.newId("minecraft:chainmail_chestplate", 64),
			IdTagValue.newId("minecraft:chainmail_leggings", 60),
			IdTagValue.newId("minecraft:chainmail_boots", 52),

			IdTagValue.newId("minecraft:golden_helmet", 72),
			IdTagValue.newId("minecraft:golden_chestplate", 96),
			IdTagValue.newId("minecraft:golden_leggings", 90),
			IdTagValue.newId("minecraft:golden_boots", 78),

			IdTagValue.newId("minecraft:iron_helmet", 72),
			IdTagValue.newId("minecraft:iron_chestplate", 96),
			IdTagValue.newId("minecraft:iron_leggings", 90),
			IdTagValue.newId("minecraft:iron_boots", 78),

			IdTagValue.newId("survivalreimagined:solarium_helmet", 48),
			IdTagValue.newId("survivalreimagined:solarium_chestplate", 64),
			IdTagValue.newId("survivalreimagined:solarium_leggings", 60),
			IdTagValue.newId("survivalreimagined:solarium_boots", 52),

			IdTagValue.newId("survivalreimagined:durium_helmet", 132),
			IdTagValue.newId("survivalreimagined:durium_chestplate", 176),
			IdTagValue.newId("survivalreimagined:durium_leggings", 165),
			IdTagValue.newId("survivalreimagined:durium_boots", 143),

			IdTagValue.newId("survivalreimagined:keego_helmet", 108),
			IdTagValue.newId("survivalreimagined:keego_chestplate", 144),
			IdTagValue.newId("survivalreimagined:keego_leggings", 135),
			IdTagValue.newId("survivalreimagined:keego_boots", 117),

			IdTagValue.newId("minecraft:diamond_helmet", 180),
			IdTagValue.newId("minecraft:diamond_chestplate", 240),
			IdTagValue.newId("minecraft:diamond_leggings", 225),
			IdTagValue.newId("minecraft:diamond_boots", 195),

			IdTagValue.newId("survivalreimagined:soul_steel_helmet", 276),
			IdTagValue.newId("survivalreimagined:soul_steel_chestplate", 368),
			IdTagValue.newId("survivalreimagined:soul_steel_leggings", 345),
			IdTagValue.newId("survivalreimagined:soul_steel_boots", 299),

			IdTagValue.newId("minecraft:netherite_helmet", 178),
			IdTagValue.newId("minecraft:netherite_chestplate", 237),
			IdTagValue.newId("minecraft:netherite_leggings", 222),
			IdTagValue.newId("minecraft:netherite_boots", 192),

			IdTagValue.newId("shieldsplus:wooden_shield", 101),
			IdTagValue.newId("shieldsplus:stone_shield", 84),
			IdTagValue.newId("survivalreimagined:flint_shield", 58),
			IdTagValue.newId("survivalreimagined:copper_shield", 42),
			IdTagValue.newId("shieldsplus:golden_shield", 72),
			IdTagValue.newId("shieldsplus:iron_shield", 187),
			IdTagValue.newId("survivalreimagined:solarium_shield", 129),
			IdTagValue.newId("survivalreimagined:durium_shield", 427),
			IdTagValue.newId("survivalreimagined:copper_coated_shield", 85),
			IdTagValue.newId("survivalreimagined:keego_shield", 285),
			IdTagValue.newId("shieldsplus:diamond_shield", 585),
			IdTagValue.newId("survivalreimagined:soul_steel_shield", 441),
			IdTagValue.newId("shieldsplus:netherite_shield", 266)
	));
	public static final ArrayList<IdTagValue> itemDurabilities = new ArrayList<>();

	public static final ArrayList<IdTagValue> TOOL_EFFICIENCIES_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/wooden", 1.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/stone", 2d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/flint", 3d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/copper", 4d),
			//new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/golden", 12d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/iron", 3.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/solarium", 2.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/durium", 3d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/coated_copper", 4.5d),
			//new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/iron+amethyst alloy", 5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/keego", 7d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/diamond", 6d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/soul_steel", 5.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/netherite", 6d)
	));
	public static final ArrayList<IdTagValue> toolEfficiencies = new ArrayList<>();

	public static final ArrayList<IdTagValue> ITEM_ATTACK_DAMAGES_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.TAG, "minecraft:axes", 6d),
			new IdTagValue(IdTagMatcher.Type.TAG, "minecraft:swords", 1d),
			new IdTagValue(IdTagMatcher.Type.TAG, "minecraft:pickaxes", 2d),
			new IdTagValue(IdTagMatcher.Type.TAG, "minecraft:shovels", 3.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "minecraft:hoes", 0d)
	));
	public static final ArrayList<IdTagValue> itemAttackDamages = new ArrayList<>();

	public static final ArrayList<IdTagValue> ITEM_ATTACK_SPEEDS_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.TAG, "minecraft:axes", 0.8d),
			new IdTagValue(IdTagMatcher.Type.TAG, "minecraft:hoes", 2.5d)
	));
	public static final ArrayList<IdTagValue> itemAttackSpeeds = new ArrayList<>();
	public static final String NO_EFFICIENCY_ITEM_LANG = "survivalreimagined.no_efficiency_item";
	public static final String BROKEN_ITEM_LANG = "survivalreimagined.broken_item";
    public static final String NO_DAMAGE_ITEM_LANG = "survivalreimagined.no_damage_item";

	@Config
	@Label(name = "More Items Tooltips", description = "If set to true items in the 'no_damage_items' and 'no_efficiency_items' will get a tooltip. Items with durability get a durability tooltip. Tools get an efficiency tooltip.")
	public static Boolean moreItemsTooltips = true;
	@Config
	@Label(name = "Unbreakable Items", description = "If set to true items will no longer break, will be left with 1 durability. Items in the survivalreimagined:not_unbreakable tag will break instead.")
	public static Boolean unbreakableItems = true;
	@Config
	@Label(name = "Unbreakable Enchanted Items", description = "If set to true items will no longer break if enchanted. Ignores the survivalreimagined:not_unbreakable item tag.")
	public static Boolean unbreakableEnchantedItems = true;

	public ItemStats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		addSyncType(new ResourceLocation(SurvivalReimagined.MOD_ID, "item_durabilities"), new SyncType(json -> loadAndReadJson(json, itemDurabilities, ITEM_DURABILITIES_DEFAULT, IdTagValue.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("item_durabilities.json", itemDurabilities, ITEM_DURABILITIES_DEFAULT, IdTagValue.LIST_TYPE, ItemStats::loadDurabilities, true, new ResourceLocation(SurvivalReimagined.MOD_ID, "item_durabilities")));
		addSyncType(new ResourceLocation(SurvivalReimagined.MOD_ID, "tool_efficiencies"), new SyncType(json -> loadAndReadJson(json, toolEfficiencies, TOOL_EFFICIENCIES_DEFAULT, IdTagValue.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("tool_efficiencies.json", toolEfficiencies, TOOL_EFFICIENCIES_DEFAULT, IdTagValue.LIST_TYPE, ItemStats::loadToolEfficiencies, true, new ResourceLocation(SurvivalReimagined.MOD_ID, "tool_efficiencies")));
		addSyncType(new ResourceLocation(SurvivalReimagined.MOD_ID, "item_attack_damages"), new SyncType(json -> loadAndReadJson(json, itemAttackDamages, ITEM_ATTACK_DAMAGES_DEFAULT, IdTagValue.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("item_attack_damages.json", itemAttackDamages, ITEM_ATTACK_DAMAGES_DEFAULT, IdTagValue.LIST_TYPE, true, new ResourceLocation(SurvivalReimagined.MOD_ID, "item_attack_damages")));
		addSyncType(new ResourceLocation(SurvivalReimagined.MOD_ID, "item_attack_speeds"), new SyncType(json -> loadAndReadJson(json, itemAttackSpeeds, ITEM_ATTACK_SPEEDS_DEFAULT, IdTagValue.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("item_attack_speeds.json", itemAttackSpeeds, ITEM_ATTACK_SPEEDS_DEFAULT, IdTagValue.LIST_TYPE, true, new ResourceLocation(SurvivalReimagined.MOD_ID, "item_attack_speeds")));
	}

	@Override
	public String getModConfigFolder() {
		return SurvivalReimagined.CONFIG_FOLDER;
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	public static void loadDurabilities(List<IdTagValue> list, boolean isclientSide) {
		for (IdTagValue durability : list) {
			List<Item> items = getAllItems(durability.id, isclientSide);
			for (Item item : items) {
				item.maxDamage = (int) durability.value;
			}
		}
	}

	public static void loadToolEfficiencies(List<IdTagValue> list, boolean isclientSide) {
		for (IdTagValue efficiency : list) {
			List<Item> items = getAllItems(efficiency.id, isclientSide);
			for (Item item : items) {
				if (!(item instanceof DiggerItem diggerItem))
					continue;
				diggerItem.speed = (float) efficiency.value;
			}
		}
	}

	protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onAttributeEvent(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;

		boolean foundAttackDamage = false;
		double ad = 0d;
		for (IdTagValue itemAttackDamage : itemAttackDamages) {
			if (itemAttackDamage.id.matchesItem(event.getItemStack())) {
				foundAttackDamage = true;
				ad = itemAttackDamage.value;
				break;
			}
		}

		boolean foundAttackSpeed = false;
		double as = 0d;
		for (IdTagValue itemAttackDamage : itemAttackSpeeds) {
			if (itemAttackDamage.id.matchesItem(event.getItemStack())) {
				foundAttackSpeed = true;
				as = itemAttackDamage.value;
				break;
			}
		}
		if (!foundAttackDamage && !foundAttackSpeed)
			return;

		double baseAd = 0d;
		if (foundAttackDamage && event.getItemStack().getItem() instanceof TieredItem tieredItem)
			baseAd = tieredItem.getTier().getAttackDamageBonus();

		Multimap<Attribute, AttributeModifier> toAdd = HashMultimap.create();
		Multimap<Attribute, AttributeModifier> toRemove = HashMultimap.create();
		for (var entry : event.getModifiers().entries()) {
			if (foundAttackDamage && entry.getValue().getId().equals(BASE_ATTACK_DAMAGE_UUID) && entry.getKey().equals(Attributes.ATTACK_DAMAGE)) {
				toAdd.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", ad + baseAd, AttributeModifier.Operation.ADDITION));
				toRemove.put(entry.getKey(), entry.getValue());
			}
			if (foundAttackSpeed && entry.getValue().getId().equals(BASE_ATTACK_SPEED_UUID) && entry.getKey().equals(Attributes.ATTACK_SPEED)) {
				toAdd.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -(4d - as), AttributeModifier.Operation.ADDITION));
				toRemove.put(entry.getKey(), entry.getValue());
			}
		}
		toRemove.forEach(event::removeModifier);
		toAdd.forEach(event::addModifier);
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
		return shouldNotBreak(stack) && stack.getDamageValue() >= stack.getMaxDamage() - 1;
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
	public void onItemUse(PlayerInteractEvent.RightClickItem event) {
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
			event.getToolTip().add(CommonComponents.space().append(Component.translatable(TOOL_EFFICIENCY_LANG, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(toolEfficiency))).withStyle(ChatFormatting.DARK_GREEN));
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