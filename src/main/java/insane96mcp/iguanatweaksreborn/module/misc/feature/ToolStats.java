package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.utils.ToolDurabilityModifier;
import insane96mcp.iguanatweaksreborn.module.misc.utils.ToolEfficiencyModifier;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Tool Stats", description = "Less durable and efficient tools")
@LoadFeature(module = Modules.Ids.MISC)
public class ToolStats extends Feature {
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> toolsDurabilityConfig;
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> toolsEfficiencyConfig;

	private static final List<String> toolsDurabilityDefault = Arrays.asList(
			"minecraft:wooden_sword,1", "minecraft:wooden_pickaxe,1", "minecraft:wooden_shovel,1", "minecraft:wooden_hoe,1", "minecraft:wooden_axe,8",
			"minecraft:stone_sword,1", "minecraft:stone_pickaxe,8", "minecraft:stone_shovel,48", "minecraft:stone_hoe,8", "minecraft:stone_axe,48",
			"minecraft:iron_sword,375", "minecraft:iron_pickaxe,375", "minecraft:iron_shovel,375", "minecraft:iron_hoe,375", "minecraft:iron_axe,375",
			"minecraft:elytra,144"
	);

	private static final List<String> toolEfficiencyDefault = Arrays.asList(
			"minecraft:wooden_pickaxe,0.8", "minecraft:wooden_shovel,0.8", "minecraft:wooden_hoe,0.8", "minecraft:wooden_axe,0.8",
			"minecraft:stone_pickaxe,0.85", "minecraft:stone_shovel,0.85", "minecraft:stone_hoe,0.85", "minecraft:stone_axe,0.85",
			"minecraft:iron_pickaxe,0.9", "minecraft:iron_shovel,0.9", "minecraft:iron_hoe,0.9", "minecraft:iron_axe,0.9",
			"minecraft:diamond_pickaxe,0.9", "minecraft:diamond_shovel,0.9", "minecraft:diamond_hoe,0.9", "minecraft:diamond_axe,0.9"
	);

	public static ArrayList<ToolDurabilityModifier> toolDurabilityModifiers;
	public static ArrayList<ToolEfficiencyModifier> toolEfficiencyModifiers;

	public ToolStats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		toolsDurabilityConfig = this.getBuilder()
				.comment("A list of items which should have their durability changed.\n" +
						"Format is 'modid:itemid,durability'")
				.worldRestart()
				.defineList("Tools Durability", toolsDurabilityDefault, o -> o instanceof String);
		toolsEfficiencyConfig = this.getBuilder()
				.comment("A list of items and multipliers that will apply to mining speed when breaking blocks with that item.\n" +
						"Format is 'modid:itemid,efficiency_multiplier'")
				.defineList("Tools Efficiency", toolEfficiencyDefault, o -> o instanceof String);
	}

	private boolean durabilityApplied = false;

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		toolEfficiencyModifiers = ToolEfficiencyModifier.parseStringList(toolsEfficiencyConfig.get());
		toolDurabilityModifiers = ToolDurabilityModifier.parseStringList(toolsDurabilityConfig.get());
		if (!this.isEnabled()
				|| durabilityApplied)
			return;
		durabilityApplied = true;
		for (ToolDurabilityModifier toolDurabilityModifier : toolDurabilityModifiers) {
			Item item = ForgeRegistries.ITEMS.getValue(toolDurabilityModifier.location);
			if (item == null) {
				LogHelper.warn("In Tool Durability Modifier the item %s doesn't exist", toolDurabilityModifier.location);
				continue;
			}
			item.maxDamage = toolDurabilityModifier.durability;
		}
	}

	@SubscribeEvent
	public void processEfficiencyMultipliers(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		Player player = event.getEntity();
		for (ToolEfficiencyModifier toolEfficiencyModifier : toolEfficiencyModifiers) {
			if (!toolEfficiencyModifier.matchesItem(player.getMainHandItem().getItem()))
				continue;
			if (!player.getMainHandItem().getItem().isCorrectToolForDrops(event.getState()))
				return;

			event.setNewSpeed(event.getNewSpeed() * toolEfficiencyModifier.efficiencyMultiplier);
			break;
		}
	}
}