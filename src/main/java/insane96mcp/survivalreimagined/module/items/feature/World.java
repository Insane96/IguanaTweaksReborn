package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.lootmodifier.LootPurgerModifier;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

@Label(name = "World", description = "Changes items in world")
@LoadFeature(module = Modules.Ids.ITEMS)
public class World extends SRFeature {

	@Config
	@Label(name = "Reduce", description = "If set to true items in the 'no_damage_items' and 'no_efficiency_items' will get a tooltip.")
	public static Boolean disabledItemsTooltip = true;

	public World(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	private static final String path = "chest_loot/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "abandoned_mineshaft", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/abandoned_mineshaft"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "buried_treasure", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/buried_treasure"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "desert_pyramid", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/desert_pyramid"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "igloo_chest", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/igloo_chest"), 2000)
				.setMultiplierAtStart(0.5f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "jungle_temple", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/jungle_temple"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "pillager_outpost", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/pillager_outpost"), 2000)
				.setMultiplierAtStart(0.5f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "ruined_portal", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/ruined_portal"), 2000)
				.setMultiplierAtStart(0.5f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "shipwreck_treasure", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/shipwreck_treasure"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "simple_dungeon", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/simple_dungeon"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "spawn_bonus_chest", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/spawn_bonus_chest"), 2000)
				.applyToDamageable()
				.build()
		);
	}
}