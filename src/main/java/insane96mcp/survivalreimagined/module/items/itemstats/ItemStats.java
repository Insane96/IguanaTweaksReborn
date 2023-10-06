package insane96mcp.survivalreimagined.module.items.itemstats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.data.IdTagValue;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Item Stats", description = "Less durable items and efficient tools. Items Durability and Efficiency are controlled via json in this feature's folder. Note that removing entries from the json requires a Minecraft Restart")
@LoadFeature(module = Modules.Ids.ITEMS)
public class ItemStats extends SRFeature {

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
			IdTagValue.newTag("survivalreimagined:equipment/hand/wooden", 33),
			IdTagValue.newTag("survivalreimagined:equipment/hand/stone", 71),
			IdTagValue.newTag("survivalreimagined:equipment/hand/flint", 55),
			IdTagValue.newTag("survivalreimagined:equipment/hand/golden", 72),
			IdTagValue.newTag("survivalreimagined:equipment/hand/copper", 42),
			IdTagValue.newTag("survivalreimagined:equipment/hand/iron", 375),
			IdTagValue.newTag("survivalreimagined:equipment/hand/solarium", 259),
			IdTagValue.newTag("survivalreimagined:equipment/hand/durium", 855),
			IdTagValue.newTag("survivalreimagined:equipment/hand/coated_copper", 240),
			IdTagValue.newTag("survivalreimagined:equipment/hand/diamond", 2341),
			IdTagValue.newTag("survivalreimagined:equipment/hand/soul_steel", 3534),
			IdTagValue.newTag("survivalreimagined:equipment/hand/keego", 1707),
			IdTagValue.newTag("survivalreimagined:equipment/hand/netherite", 3047),

			IdTagValue.newId("minecraft:elytra", 144),
			IdTagValue.newId("minecraft:carrot_on_a_stick", 63),
			IdTagValue.newId("minecraft:fishing_rod", 33),
			IdTagValue.newId("minecraft:shears", 87),
			IdTagValue.newId("minecraft:trident", 375),

			IdTagValue.newId("minecraft:leather_helmet", 76),
			IdTagValue.newId("minecraft:leather_chestplate", 101),
			IdTagValue.newId("minecraft:leather_leggings", 95),
			IdTagValue.newId("minecraft:leather_boots", 82),

			IdTagValue.newId("minecraft:chainmail_helmet", 90),
			IdTagValue.newId("minecraft:chainmail_chestplate", 120),
			IdTagValue.newId("minecraft:chainmail_leggings", 113),
			IdTagValue.newId("minecraft:chainmail_boots", 98),

			IdTagValue.newId("survivalreimagined:chained_copper_helmet", 36),
			IdTagValue.newId("survivalreimagined:chained_copper_chestplate", 48),
			IdTagValue.newId("survivalreimagined:chained_copper_leggings", 45),
			IdTagValue.newId("survivalreimagined:chained_copper_boots", 39),

			IdTagValue.newId("minecraft:iron_helmet", 72),
			IdTagValue.newId("minecraft:iron_chestplate", 96),
			IdTagValue.newId("minecraft:iron_leggings", 90),
			IdTagValue.newId("minecraft:iron_boots", 78),

			IdTagValue.newId("survivalreimagined:solarium_helmet", 72),
			IdTagValue.newId("survivalreimagined:solarium_chestplate", 96),
			IdTagValue.newId("survivalreimagined:solarium_leggings", 90),
			IdTagValue.newId("survivalreimagined:solarium_boots", 78),

			IdTagValue.newId("survivalreimagined:durium_helmet", 144),
			IdTagValue.newId("survivalreimagined:durium_chestplate", 192),
			IdTagValue.newId("survivalreimagined:durium_leggings", 180),
			IdTagValue.newId("survivalreimagined:durium_boots", 156),

			IdTagValue.newId("minecraft:golden_helmet", 72),
			IdTagValue.newId("minecraft:golden_chestplate", 96),
			IdTagValue.newId("minecraft:golden_leggings", 90),
			IdTagValue.newId("minecraft:golden_boots", 78),

			IdTagValue.newId("minecraft:diamond_helmet", 158),
			IdTagValue.newId("minecraft:diamond_chestplate", 211),
			IdTagValue.newId("minecraft:diamond_leggings", 198),
			IdTagValue.newId("minecraft:diamond_boots", 172),

			IdTagValue.newId("survivalreimagined:soul_steel_helmet", 252),
			IdTagValue.newId("survivalreimagined:soul_steel_chestplate", 336),
			IdTagValue.newId("survivalreimagined:soul_steel_leggings", 315),
			IdTagValue.newId("survivalreimagined:soul_steel_boots", 273),

			IdTagValue.newId("survivalreimagined:keego_helmet", 132),
			IdTagValue.newId("survivalreimagined:keego_chestplate", 176),
			IdTagValue.newId("survivalreimagined:keego_leggings", 165),
			IdTagValue.newId("survivalreimagined:keego_boots", 143),

			IdTagValue.newId("minecraft:netherite_helmet", 178),
			IdTagValue.newId("minecraft:netherite_chestplate", 237),
			IdTagValue.newId("minecraft:netherite_leggings", 222),
			IdTagValue.newId("minecraft:netherite_boots", 192),

			IdTagValue.newId("shieldsplus:wooden_shield", 32),
			IdTagValue.newId("survivalreimagined:flint_shield", 82),
			IdTagValue.newId("shieldsplus:golden_shield", 95),
			IdTagValue.newId("survivalreimagined:copper_coated_shield", 230),
			IdTagValue.newId("survivalreimagined:durium_shield", 672),
			IdTagValue.newId("shieldsplus:diamond_shield", 602),
			IdTagValue.newId("survivalreimagined:soul_steel_shield", 865),
			IdTagValue.newId("survivalreimagined:keego_shield", 552),
			IdTagValue.newId("shieldsplus:netherite_shield", 1044)
	));
	public static final ArrayList<IdTagValue> itemDurabilities = new ArrayList<>();

	public static final ArrayList<IdTagValue> TOOL_EFFICIENCIES_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/wooden", 1.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/stone", 2.25d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/flint", 3d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/copper", 4.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/iron", 3.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/solarium", 2.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/durium", 3.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/coated_copper", 6.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/diamond", 6d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/soul_steel", 6d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/keego", 8d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/netherite", 6.5d)
	));
	public static final ArrayList<IdTagValue> toolEfficiencies = new ArrayList<>();
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
		JSON_CONFIGS.add(new JsonConfig<>("item_durabilities.json", itemDurabilities, ITEM_DURABILITIES_DEFAULT, IdTagValue.LIST_TYPE, ItemStats::loadDurabilities, true, JsonConfigSyncMessage.ConfigType.DURABILITY));
		JSON_CONFIGS.add(new JsonConfig<>("tool_efficiencies.json", toolEfficiencies, TOOL_EFFICIENCIES_DEFAULT, IdTagValue.LIST_TYPE, ItemStats::loadToolEfficiencies, true, JsonConfigSyncMessage.ConfigType.EFFICIENCIES));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
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

	public static void handleDurabilityPacket(String json) {
		loadAndReadJson(json, itemDurabilities, ITEM_DURABILITIES_DEFAULT, IdTagValue.LIST_TYPE);
		//loadDurabilities(itemDurabilities, true);
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

	public static void handleEfficienciesPacket(String json) {
		loadAndReadJson(json, toolEfficiencies, TOOL_EFFICIENCIES_DEFAULT, IdTagValue.LIST_TYPE);
		//loadToolEfficiencies(toolEfficiencies, true);
	}

	@SubscribeEvent
	public void onDataPackSync(OnDatapackSyncEvent event) {
		Gson gson = new GsonBuilder().create();
		if (event.getPlayer() == null) {
			event.getPlayerList().getPlayers().forEach(player -> {
				JsonConfigSyncMessage.sync(JsonConfigSyncMessage.ConfigType.DURABILITY, gson.toJson(itemDurabilities, IdTagValue.LIST_TYPE), player);
				JsonConfigSyncMessage.sync(JsonConfigSyncMessage.ConfigType.EFFICIENCIES, gson.toJson(toolEfficiencies, IdTagValue.LIST_TYPE), player);
			});
		}
		else {
			JsonConfigSyncMessage.sync(JsonConfigSyncMessage.ConfigType.DURABILITY, gson.toJson(itemDurabilities, IdTagValue.LIST_TYPE), event.getPlayer());
			JsonConfigSyncMessage.sync(JsonConfigSyncMessage.ConfigType.EFFICIENCIES, gson.toJson(toolEfficiencies, IdTagValue.LIST_TYPE), event.getPlayer());
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