package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.utils.IdTagValue;
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Tool Stats", description = "Less durable and efficient tools. Tools Durability and Efficiency are controlled via json in this feature's folder. Note that removing entries from the json requires a Minecraft Restart")
@LoadFeature(module = Modules.Ids.ITEMS)
public class ToolStats extends SRFeature {

	public static final String TOOL_EFFICIENCY = "survivalreimagined.tool_efficiency";
	public static final String BONUS_TOOL_EFFICIENCY = "survivalreimagined.bonus_tool_efficiency";
	public static final ResourceLocation NO_DAMAGE_ITEMS = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_damage_items");
	public static final ResourceLocation NO_EFFICIENCY_ITEMS = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_efficiency_items");

	public static final ArrayList<IdTagValue> ITEM_DURABILITIES_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_sword", 1),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_pickaxe", 33),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_axe", 33),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_shovel", 33),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_hoe", 33),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_sword", 1),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_pickaxe", 89),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_axe", 89),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_shovel", 89),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_hoe", 89),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:elytra", 86),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:carrot_on_a_stick", 63),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:shield", 268),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:shears", 119),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:leather_helmet", 39),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:leather_chestplate", 48),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:leather_leggings", 45),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:leather_boots", 33),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_helmet", 91),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_chestplate", 112),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_leggings", 105),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chainmail_boots", 77),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_helmet", 91),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_chestplate", 112),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_leggings", 105),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_boots", 77),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_helmet", 45),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_chestplate", 56),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_leggings", 52),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:golden_boots", 38),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_helmet", 214),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_chestplate", 264),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_leggings", 247),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_boots", 181),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_helmet", 214),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_chestplate", 264),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_leggings", 247),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_boots", 181),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_helmet", 260),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_chestplate", 320),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_leggings", 300),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_boots", 220)
	));
	public static final ArrayList<IdTagValue> itemDurabilities = new ArrayList<>();

	public static final ArrayList<IdTagValue> TOOL_EFFICIENCIES_DEFAULT = new ArrayList<>(Arrays.asList(
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/stone", 2.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/iron", 4.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/tools/diamond", 7d)
	));
	public static final ArrayList<IdTagValue> toolEfficiencies = new ArrayList<>();

	@Config
	@Label(name = "Disabled items tooltip", description = "If set to true items in the 'no_damage_items' and 'no_efficiency_items' will get a tooltip.")
	public static Boolean disabledItemsTooltip = true;

	public ToolStats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
		this.loadAndReadFile("item_durabilities.json", itemDurabilities, ITEM_DURABILITIES_DEFAULT, IdTagValue.LIST_TYPE);
		this.loadAndReadFile("tool_efficiencies.json", toolEfficiencies, TOOL_EFFICIENCIES_DEFAULT, IdTagValue.LIST_TYPE);

		for (IdTagValue durability : itemDurabilities) {
			List<Item> items = getAllItems(durability);
			for (Item item : items) {
				item.maxDamage = (int) durability.value;
			}
		}

		for (IdTagValue efficiency : toolEfficiencies) {
			List<Item> items = getAllItems(efficiency);
			for (Item item : items) {
				if (!(item instanceof DiggerItem diggerItem))
					continue;
				diggerItem.speed = (float) efficiency.value;
			}
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

	static final DecimalFormat ONE_DECIMAL_FORMATTER = new DecimalFormat("#.#");

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
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
			event.getToolTip().add(Component.translatable(TOOL_EFFICIENCY, ONE_DECIMAL_FORMATTER.format(toolEfficiency)).withStyle(ChatFormatting.DARK_GREEN));
			if (efficiency > 0)
				event.getToolTip().add(Component.literal("  ").append(Component.translatable(BONUS_TOOL_EFFICIENCY, ONE_DECIMAL_FORMATTER.format(bonusToolEfficiency)).withStyle(ChatFormatting.GRAY)));
		}
	}
}