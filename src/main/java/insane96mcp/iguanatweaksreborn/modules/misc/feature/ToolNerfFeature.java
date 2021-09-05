package insane96mcp.iguanatweaksreborn.modules.misc.feature;

import insane96mcp.iguanatweaksreborn.modules.misc.classutils.ToolDurability;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Tool Nerf", description = "Less durable tools. Changing this config options requires a Minecraft restart")
public class ToolNerfFeature extends Feature {
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> toolsDurabilityConfig;

	private final List<String> toolsDurabilityDefault = Arrays.asList(
			"minecraft:wooden_sword,1", "minecraft:wooden_pickaxe,1", "minecraft:wooden_shovel,1", "minecraft:wooden_hoe,1", "minecraft:wooden_axe,10",
			"minecraft:stone_sword,6", "minecraft:stone_pickaxe,6", "minecraft:stone_shovel,50", "minecraft:stone_hoe,6", "minecraft:stone_axe,50"
	);

	public ArrayList<ToolDurability> toolsDurability;

	public ToolNerfFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		toolsDurabilityConfig = Config.builder
				.comment("A list of items which should have their durability changed.\n" +
						"Format is 'modid:itemid,durability'")
				.defineList("Tools Durability", toolsDurabilityDefault, o -> o instanceof String);
		Config.builder.pop();
	}

	private boolean durabilityApplied = false;

	@Override
	public void loadConfig() {
		super.loadConfig();
		toolsDurability = ToolDurability.parseList(toolsDurabilityConfig.get());
		if (!this.isEnabled())
			return;
		if (durabilityApplied)
			return;
		for (ToolDurability toolDurability : toolsDurability) {
			Item item = ForgeRegistries.ITEMS.getValue(toolDurability.id);
			if (item == null) {
				LogHelper.info("In Tool Nerf the item %s doesn't exist", toolDurability.id);
				continue;
			}
			item.maxDamage = toolDurability.durability;
		}

		durabilityApplied = true;
	}
}
