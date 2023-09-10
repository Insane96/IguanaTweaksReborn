package insane96mcp.survivalreimagined.module.items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.shieldsplus.setup.SPItems;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.IdTagValue;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import insane96mcp.survivalreimagined.setup.Strings;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Item Stats", description = "Less durable items and efficient tools. Items Durability and Efficiency are controlled via json in this feature's folder. Note that removing entries from the json requires a Minecraft Restart")
@LoadFeature(module = Modules.Ids.ITEMS)
public class ItemStats extends SRFeature {

	public static final String TOOL_EFFICIENCY_LANG = "survivalreimagined.tool_efficiency";
	public static final String TOOL_DURABILITY_LANG = "survivalreimagined.tool_durability";
	public static final TagKey<Item> NO_DAMAGE = SRItemTagsProvider.create("no_damage");
	public static final TagKey<Item> NO_EFFICIENCY = SRItemTagsProvider.create("no_efficiency");

	public static final ArrayList<IdTagValue> ITEM_DURABILITIES_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/wooden", 33),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/stone", 71),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/flint", 55),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/golden", 72),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/copper", 42),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/iron", 375),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/solarium", 259),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/durium", 855),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/coated_copper", 482),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/diamond", 2341),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/soul_steel", 3534),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/keego", 2007),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/netherite", 3047),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:elytra", 86),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:carrot_on_a_stick", 63),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:fishing_rod", 33),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:shears", 119),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:trident", 375),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_helmet", 108),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_chestplate", 144),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_leggings", 135),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_boots", 117),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:chained_copper_helmet", 36),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:chained_copper_chestplate", 48),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:chained_copper_leggings", 45),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:chained_copper_boots", 39),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_helmet", 72),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_chestplate", 96),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_leggings", 90),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_boots", 78),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:solarium_helmet", 72),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:solarium_chestplate", 96),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:solarium_leggings", 90),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:solarium_boots", 78),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:durium_helmet", 120),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:durium_chestplate", 160),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:durium_leggings", 150),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:durium_boots", 130),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_helmet", 72),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_chestplate", 96),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_leggings", 90),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_boots", 78),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_helmet", 238),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_chestplate", 317),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_leggings", 297),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_boots", 257),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_helmet", 252),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_chestplate", 336),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_leggings", 315),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_boots", 273),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:keego_helmet", 158),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:keego_chestplate", 211),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:keego_leggings", 198),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:keego_boots", 172),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_helmet", 222),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_chestplate", 296),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_leggings", 278),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_boots", 241),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:flint_shield", 82),
			new IdTagValue(IdTagMatcher.Type.ID, "shieldsplus:golden_shield", 95),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:copper_coated_shield", 230),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:durium_shield", 672),
			new IdTagValue(IdTagMatcher.Type.ID, "shieldsplus:diamond_shield", 602),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_shield", 865),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:keego_shield", 552),
			new IdTagValue(IdTagMatcher.Type.ID, "shieldsplus:netherite_shield", 1044)
	));
	public static final ArrayList<IdTagValue> itemDurabilities = new ArrayList<>();

	public static final ArrayList<IdTagValue> TOOL_EFFICIENCIES_DEFAULT = new ArrayList<>(Arrays.asList(
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/wooden", 1.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/stone", 2.25d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/flint", 3d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/copper", 4.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/iron", 3.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/solarium", 3d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/durium", 4d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/coated_copper", 7.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/diamond", 6d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/soul_steel", 6d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/keego", 8d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/netherite", 6.5d)
	));
	public static final ArrayList<IdTagValue> toolEfficiencies = new ArrayList<>();

	@Config
	@Label(name = "Override shield blocking damage", description = "If true copper shield will block 4 damage and Golden shields will block 100 damage. Requires a restart.")
	public static Boolean overrideShieldBlockDamage = true;

	@Config
	@Label(name = "More Items Tooltips", description = "If set to true items in the 'no_damage_items' and 'no_efficiency_items' will get a tooltip. Items with durability get a durability tooltip. Tools get an efficiency tooltip.")
	public static Boolean moreItemsTooltips = true;

	public ItemStats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("item_durabilities.json", itemDurabilities, ITEM_DURABILITIES_DEFAULT, IdTagValue.LIST_TYPE, ItemStats::loadDurabilities, true, JsonConfigSyncMessage.ConfigType.DURABILITIES));
		JSON_CONFIGS.add(new JsonConfig<>("tool_efficiencies.json", toolEfficiencies, TOOL_EFFICIENCIES_DEFAULT, IdTagValue.LIST_TYPE, ItemStats::loadToolEfficiencies, true, JsonConfigSyncMessage.ConfigType.EFFICIENCIES));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
		if (overrideShieldBlockDamage) {
			SPItems.COPPER_SHIELD.get().blockingDamageOverride = 4d;
			SPItems.GOLDEN_SHIELD.get().blockingDamageOverride = 3d;
			SPItems.DIAMOND_SHIELD.get().blockingDamageOverride = 6.5d;
		}
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	public static void loadDurabilities(List<IdTagValue> list, boolean isclientSide) {
		for (IdTagValue durability : list) {
			List<Item> items = getAllItems(durability, isclientSide);
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
			List<Item> items = getAllItems(efficiency, isclientSide);
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
				JsonConfigSyncMessage.sync(JsonConfigSyncMessage.ConfigType.DURABILITIES, gson.toJson(itemDurabilities, IdTagValue.LIST_TYPE), player);
				JsonConfigSyncMessage.sync(JsonConfigSyncMessage.ConfigType.EFFICIENCIES, gson.toJson(toolEfficiencies, IdTagValue.LIST_TYPE), player);
			});
		}
		else {
			JsonConfigSyncMessage.sync(JsonConfigSyncMessage.ConfigType.DURABILITIES, gson.toJson(itemDurabilities, IdTagValue.LIST_TYPE), event.getPlayer());
			JsonConfigSyncMessage.sync(JsonConfigSyncMessage.ConfigType.EFFICIENCIES, gson.toJson(toolEfficiencies, IdTagValue.LIST_TYPE), event.getPlayer());
		}
	}

	@SubscribeEvent
	public void processEfficiencyMultipliers(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		Player player = event.getEntity();
		if (Utils.isItemInTag(player.getMainHandItem().getItem(), NO_EFFICIENCY)) {
			event.setCanceled(true);
			event.getEntity().displayClientMessage(Component.translatable(Strings.Translatable.NO_EFFICIENCY_ITEM), true);
		}
	}

	@SubscribeEvent
	public void processAttackDamage(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof Player player))
			return;

		if (Utils.isItemInTag(player.getMainHandItem().getItem(), NO_DAMAGE)) {
			event.setAmount(1f);
			player.displayClientMessage(Component.translatable(Strings.Translatable.NO_DAMAGE_ITEM), true);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !moreItemsTooltips)
			return;

		if (Utils.isItemInTag(event.getItemStack().getItem(), NO_DAMAGE)) {
			event.getToolTip().add(Component.translatable(Strings.Translatable.NO_DAMAGE_ITEM).withStyle(ChatFormatting.RED));
		}
		if (Utils.isItemInTag(event.getItemStack().getItem(), NO_EFFICIENCY)) {
			event.getToolTip().add(Component.translatable(Strings.Translatable.NO_EFFICIENCY_ITEM).withStyle(ChatFormatting.RED));
		}
		else if (event.getItemStack().getItem() instanceof DiggerItem diggerItem){
			int lvl = event.getItemStack().getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
			float toolEfficiency = diggerItem.speed;
			float bonusToolEfficiency = EnchantmentsFeature.getEfficiencyBonus(toolEfficiency, lvl);
			if (lvl > 0)
				toolEfficiency += bonusToolEfficiency;
			event.getToolTip().add(CommonComponents.space().append(Component.translatable(TOOL_EFFICIENCY_LANG, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(toolEfficiency))).withStyle(ChatFormatting.DARK_GREEN));
		}

		if (event.getItemStack().isDamageableItem()) {
			MutableComponent component = Component.translatable(TOOL_DURABILITY_LANG, event.getItemStack().getMaxDamage() - event.getItemStack().getDamageValue(), event.getItemStack().getMaxDamage()).withStyle(ChatFormatting.GRAY);
			if (event.getItemStack().getAllEnchantments().containsKey(Enchantments.UNBREAKING)) {
				int lvl = event.getItemStack().getAllEnchantments().get(Enchantments.UNBREAKING);
				component.append(Component.literal(" (+%.0f%%)".formatted(getUnbreakingPercentageBonus(lvl) * 100f)).withStyle(ChatFormatting.LIGHT_PURPLE));
			}
			event.getToolTip().add(component);
		}
	}

	private static float getUnbreakingPercentageBonus(int lvl) {
		return 1f / (1f - EnchantmentsFeature.unbreakingBonus(lvl)) - 1f;
	}
}