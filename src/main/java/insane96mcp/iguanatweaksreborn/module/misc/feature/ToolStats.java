package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.utils.IdTagValue;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Tool Stats", description = "Less durable and efficient tools. Tools Durabilities and Efficiencies are controlled via json in this feature's folder. Note that removing entries from the json requires a Minecraft Restart")
@LoadFeature(module = Modules.Ids.MISC)
public class ToolStats extends ITFeature {
	public static final ResourceLocation NO_DAMAGE_ITEMS = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "no_damage_items");
	public static final ResourceLocation NO_EFFICIENCY_ITEMS = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "no_efficiency_items");

	public static final ArrayList<IdTagValue> TOOL_DURABILITIES_DEFAULT = new ArrayList<>(Arrays.asList(
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_sword", 1),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_pickaxe", 1),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_axe", 8),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_shovel", 1),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:wooden_hoe", 1),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_sword", 1),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_pickaxe", 8),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_axe", 48),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_shovel", 48),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:stone_hoe", 8),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_sword", 375),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_pickaxe", 375),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_axe", 375),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_shovel", 375),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_hoe", 375),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:elytra", 144)
	));
	public static final ArrayList<IdTagValue> toolDurabilities = new ArrayList<>();

	public static final ArrayList<IdTagValue> TOOL_EFFICIENCIES_DEFAULT = new ArrayList<>(Arrays.asList(
			new IdTagValue(IdTagMatcher.Type.TAG, "iguanatweaksreborn:equipment/hand/tools/wooden", 1.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "iguanatweaksreborn:equipment/hand/tools/stone", 3d),
			new IdTagValue(IdTagMatcher.Type.TAG, "iguanatweaksreborn:equipment/hand/tools/iron", 5.5d),
			new IdTagValue(IdTagMatcher.Type.TAG, "iguanatweaksreborn:equipment/hand/tools/diamond", 7.5d)
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
		this.loadAndReadFile("tool_durabilities.json", toolDurabilities, TOOL_DURABILITIES_DEFAULT, IdTagValue.LIST_TYPE);
		this.loadAndReadFile("tool_efficiencies.json", toolEfficiencies, TOOL_EFFICIENCIES_DEFAULT, IdTagValue.LIST_TYPE);

		for (IdTagValue durability : toolDurabilities) {
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

	}
}