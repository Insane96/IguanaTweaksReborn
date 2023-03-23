package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.lootmodifier.LootPurgerModifier;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.List;

@Label(name = "Loot reducer", description = "Reduces loot from some structures when too near spawn.")
@LoadFeature(module = Modules.Ids.WORLD)
public class Loot extends SRFeature {

	//TODO Check if changing day length is viable

	public Loot(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	private static final String path = "chest_loot/";

	private static final List<ResourceLocation> VILLAGERS_LOOT = List.of(
			new ResourceLocation("minecraft:chests/village/village_armorer"),
			new ResourceLocation("minecraft:chests/village/village_butcher"),
			new ResourceLocation("minecraft:chests/village/village_cartographer"),
			new ResourceLocation("minecraft:chests/village/village_desert_house"),
			new ResourceLocation("minecraft:chests/village/village_fisher"),
			new ResourceLocation("minecraft:chests/village/village_fletcher"),
			new ResourceLocation("minecraft:chests/village/village_mason"),
			new ResourceLocation("minecraft:chests/village/village_plains_house"),
			new ResourceLocation("minecraft:chests/village/village_savanna_house"),
			new ResourceLocation("minecraft:chests/village/village_shepherd"),
			new ResourceLocation("minecraft:chests/village/village_snowy_house"),
			new ResourceLocation("minecraft:chests/village/village_taiga_house"),
			new ResourceLocation("minecraft:chests/village/village_tannery"),
			new ResourceLocation("minecraft:chests/village/village_temple"),
			new ResourceLocation("minecraft:chests/village/village_toolsmith"),
			new ResourceLocation("minecraft:chests/village/village_weaponsmith")
	);

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "chests/abandoned_mineshaft", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/abandoned_mineshaft"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/buried_treasure", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/buried_treasure"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/desert_pyramid", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/desert_pyramid"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/igloo_chest", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/igloo_chest"), 2000)
				.setMultiplierAtStart(0.5f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/jungle_temple", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/jungle_temple"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/pillager_outpost", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/pillager_outpost"), 2000)
				.setMultiplierAtStart(0.5f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/ruined_portal", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/ruined_portal"), 2000)
				.setMultiplierAtStart(0.5f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/shipwreck_treasure", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/shipwreck_treasure"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/simple_dungeon", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/simple_dungeon"), 2000)
				.setMultiplierAtStart(0.25f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/spawn_bonus_chest", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/spawn_bonus_chest"), 2000)
				.applyToDamageable()
				.build()
		);
		for (ResourceLocation village : VILLAGERS_LOOT) {
			provider.add(path + village.getPath(), new LootPurgerModifier.Builder(village, 2000)
					.setMultiplierAtStart(0.1f)
					.applyToDamageable()
					.build()
			);
		}
	}
}