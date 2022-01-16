package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.module.misc.utils.ToolDurabilityModifier;
import insane96mcp.iguanatweaksreborn.module.misc.utils.ToolEfficiencyModifier;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Tool Stats", description = "Less durable and efficient tools. Changing this config's options requires a Minecraft restart")
public class ToolStats extends Feature {
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> toolsDurabilityConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> toolsEfficiencyConfig;

	private final List<String> toolsDurabilityDefault = Arrays.asList(
			"minecraft:wooden_sword,1", "minecraft:wooden_pickaxe,1", "minecraft:wooden_shovel,1", "minecraft:wooden_hoe,1", "minecraft:wooden_axe,8",
			"minecraft:stone_sword,6", "minecraft:stone_pickaxe,7", "minecraft:stone_shovel,48", "minecraft:stone_hoe,3", "minecraft:stone_axe,48",
			"minecraft:iron_sword,375", "minecraft:iron_pickaxe,375", "minecraft:iron_shovel,375", "minecraft:iron_hoe,375", "minecraft:iron_axe,375"
	);

	private final List<String> toolEfficiencyDefault = Arrays.asList(
			"minecraft:wooden_pickaxe,0.75", "minecraft:wooden_shovel,0.75", "minecraft:wooden_hoe,0.75", "minecraft:wooden_axe,0.75",
			"minecraft:stone_pickaxe,0.75", "minecraft:stone_shovel,0.75", "minecraft:stone_hoe,0.75", "minecraft:stone_axe,0.75",
			"minecraft:iron_pickaxe,0.9", "minecraft:iron_shovel,0.9", "minecraft:iron_hoe,0.9", "minecraft:iron_axe,0.9",
			"minecraft:diamond_pickaxe,0.9", "minecraft:diamond_shovel,0.9", "minecraft:diamond_hoe,0.9", "minecraft:diamond_axe,0.9"
	);

	public ArrayList<ToolDurabilityModifier> toolDurabilityModifiers;
	public ArrayList<ToolEfficiencyModifier> toolEfficiencyModifiers;

	public ToolStats(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		toolsDurabilityConfig = Config.builder
				.comment("A list of items which should have their durability changed.\n" +
						"Format is 'modid:itemid,durability'")
				.defineList("Tools Durability", toolsDurabilityDefault, o -> o instanceof String);
		toolsEfficiencyConfig = Config.builder
				.comment("A list of items and multipliers that will apply to mining speed when breaking blocks with that item.\n" +
						"Format is 'modid:itemid,efficiency_multiplier'")
				.defineList("Tools Efficiency", toolEfficiencyDefault, o -> o instanceof String);
		Config.builder.pop();
	}

	private boolean durabilityApplied = false;

	@Override
	public void loadConfig() {
		super.loadConfig();
		toolEfficiencyModifiers = ToolEfficiencyModifier.parseList(toolsEfficiencyConfig.get());
		toolDurabilityModifiers = ToolDurabilityModifier.parseList(toolsDurabilityConfig.get());
		if (!this.isEnabled())
			return;
		if (durabilityApplied)
			return;
		durabilityApplied = true;
		for (ToolDurabilityModifier toolDurabilityModifier : toolDurabilityModifiers) {
			Item item = ForgeRegistries.ITEMS.getValue(toolDurabilityModifier.id);
			if (item == null) {
				LogHelper.info("In Tool Durability Modifier the item %s doesn't exist", toolDurabilityModifier.id);
				continue;
			}
			item.maxDamage = toolDurabilityModifier.durability;
		}
	}

	@SubscribeEvent
	public void processEfficiencyMultipliers(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		Player player = event.getPlayer();
		for (ToolEfficiencyModifier toolEfficiencyModifier : this.toolEfficiencyModifiers) {
			if (!player.getMainHandItem().getItem().getRegistryName().equals(toolEfficiencyModifier.id))
				continue;
			if (!player.getMainHandItem().getItem().isCorrectToolForDrops(event.getState()))
				return;

			event.setNewSpeed(event.getNewSpeed() * toolEfficiencyModifier.efficiencyMultiplier);
			break;
		}
	}
}