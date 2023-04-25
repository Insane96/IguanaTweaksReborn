package insane96mcp.survivalreimagined.module.items.feature;

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
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.utils.IdTagValue;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import insane96mcp.survivalreimagined.setup.Strings;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

	public static final String TOOL_EFFICIENCY = "survivalreimagined.tool_efficiency";
	public static final String BONUS_TOOL_EFFICIENCY = "survivalreimagined.bonus_tool_efficiency";
	public static final ResourceLocation NO_DAMAGE_ITEMS = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_damage_items");
	public static final ResourceLocation NO_EFFICIENCY_ITEMS = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_efficiency_items");

	public static final ArrayList<IdTagValue> ITEM_DURABILITIES_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/wooden", 33),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/flint", 48),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/stone", 63),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/copper", 75),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/golden", 52),

			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/netherite", 3047),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:elytra", 86),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:carrot_on_a_stick", 63),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:fishing_rod", 33),
			//new IdTagValue(IdTagMatcher.Type.ID, "minecraft:shield", 268),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:shears", 119),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:leather_helmet", 33),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:leather_chestplate", 48),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:leather_leggings", 45),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:leather_boots", 39),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_helmet", 77),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_chestplate", 112),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_leggings", 105),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_boots", 91),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:chained_copper_helmet", 66),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:chained_copper_chestplate", 96),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:chained_copper_leggings", 90),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:chained_copper_boots", 78),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_helmet", 82),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_chestplate", 120),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_leggings", 112),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_boots", 97),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:florpium_helmet", 110),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:florpium_chestplate", 160),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:florpium_leggings", 150),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:florpium_boots", 130),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_helmet", 66),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_chestplate", 96),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_leggings", 90),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_boots", 78),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_helmet", 181),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_chestplate", 264),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_leggings", 247),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_boots", 214),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_helmet", 253),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_chestplate", 368),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_leggings", 345),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_boots", 299)
	));
	public static final ArrayList<IdTagValue> itemDurabilities = new ArrayList<>();

	public static final ArrayList<IdTagValue> TOOL_EFFICIENCIES_DEFAULT = new ArrayList<>(Arrays.asList(
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/wooden", 1.75d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/flint", 3.333d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/copper", 6d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/stone", 2.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/iron", 4d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/florpium", 4.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/golden", 15d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/diamond", 7d)
	));
	public static final ArrayList<IdTagValue> toolEfficiencies = new ArrayList<>();

	@Config
	@Label(name = "Disabled items tooltip", description = "If set to true items in the 'no_damage_items' and 'no_efficiency_items' will get a tooltip.")
	public static Boolean disabledItemsTooltip = true;

	public ItemStats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("item_durabilities.json", itemDurabilities, ITEM_DURABILITIES_DEFAULT, IdTagValue.LIST_TYPE, ItemStats::loadDurabilities, true, JsonConfigSyncMessage.ConfigType.DURABILITIES));
		JSON_CONFIGS.add(new JsonConfig<>("tool_efficiencies.json", toolEfficiencies, TOOL_EFFICIENCIES_DEFAULT, IdTagValue.LIST_TYPE, ItemStats::loadToolEfficiencies, true, JsonConfigSyncMessage.ConfigType.EFFICIENCIES));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
		SPItems.COPPER_SHIELD.get().blockingDamageOverride = 4d;
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
		if (Utils.isItemInTag(player.getMainHandItem().getItem(), NO_EFFICIENCY_ITEMS)) {
			event.setCanceled(true);
			event.getEntity().displayClientMessage(Component.translatable(Strings.Translatable.NO_EFFICIENCY_ITEM), true);
		}
	}

	@SubscribeEvent
	public void processAttackDamage(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof Player player))
			return;

		if (Utils.isItemInTag(player.getMainHandItem().getItem(), NO_DAMAGE_ITEMS)) {
			event.setAmount(1f);
			player.displayClientMessage(Component.translatable(Strings.Translatable.NO_DAMAGE_ITEM), true);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !disabledItemsTooltip)
			return;

		if (Utils.isItemInTag(event.getItemStack().getItem(), NO_DAMAGE_ITEMS)) {
			event.getToolTip().add(Component.translatable(Strings.Translatable.NO_DAMAGE_ITEM).withStyle(ChatFormatting.RED));
		}
		if (Utils.isItemInTag(event.getItemStack().getItem(), NO_EFFICIENCY_ITEMS)) {
			event.getToolTip().add(Component.translatable(Strings.Translatable.NO_EFFICIENCY_ITEM).withStyle(ChatFormatting.RED));
		}
		else if (event.getItemStack().getItem() instanceof DiggerItem diggerItem){
			int efficiency = event.getItemStack().getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
			float toolEfficiency = diggerItem.speed;
			float bonusToolEfficiency = diggerItem.speed * (efficiency * 0.75f);
			if (efficiency > 0)
				toolEfficiency += bonusToolEfficiency;
			event.getToolTip().add(Component.literal(" ").append(Component.translatable(TOOL_EFFICIENCY, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(toolEfficiency))).withStyle(ChatFormatting.DARK_GREEN));
			/*if (efficiency > 0)
				event.getToolTip().add(Component.literal("  ").append(Component.translatable(BONUS_TOOL_EFFICIENCY, ONE_DECIMAL_FORMATTER.format(bonusToolEfficiency)).withStyle(ChatFormatting.GRAY)));*/
		}
	}
}