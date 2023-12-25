package insane96mcp.iguanatweaksreborn.module.world;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.iguanatweaksreborn.data.lootmodifier.LootPurgerModifier;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.IntegratedDataPack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.List;

@Label(name = "Loot reducer", description = "Reduces loot from some structures when too near spawn.")
@LoadFeature(module = Modules.Ids.WORLD)
public class Loot extends Feature {

	@Config
	@Label(name = "Better Structure Loot", description = "If true a datapack will be enabled that overhauls structure loot")
	public static Boolean betterStructureLoot = true;

	public Loot(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "better_loot", Component.literal("Survival Reimagined Better Loot"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && betterStructureLoot));
	}

	private static final String path = "world_loot/";

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


	private record WoodTypes(Item log, Item planks, String name) {}
	private static final List<WoodTypes> WOOD_TYPES = List.of(
			new WoodTypes(Items.OAK_LOG, Items.OAK_PLANKS, "oak"),
			new WoodTypes(Items.BIRCH_LOG, Items.BIRCH_PLANKS, "birch"),
			new WoodTypes(Items.SPRUCE_LOG, Items.SPRUCE_PLANKS, "spruce"),
			new WoodTypes(Items.JUNGLE_LOG, Items.JUNGLE_PLANKS, "jungle"),
			new WoodTypes(Items.ACACIA_LOG, Items.ACACIA_PLANKS, "acacia"),
			new WoodTypes(Items.DARK_OAK_LOG, Items.DARK_OAK_PLANKS, "dark_oak"),
			new WoodTypes(Items.WARPED_STEM, Items.WARPED_PLANKS, "warped"),
			new WoodTypes(Items.CRIMSON_STEM, Items.CRIMSON_PLANKS, "crimson")
	);

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "chests/abandoned_mineshaft", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/abandoned_mineshaft"), 3000)
				.setMultiplierAtStart(0.1f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/buried_treasure", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/buried_treasure"), 3000)
				.setMultiplierAtStart(0.1f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/desert_pyramid", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/desert_pyramid"), 3000)
				.setMultiplierAtStart(0.1f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/igloo_chest", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/igloo_chest"), 3000)
				.setMultiplierAtStart(0.1f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/jungle_temple", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/jungle_temple"), 3000)
				.setMultiplierAtStart(0.1f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/pillager_outpost", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/pillager_outpost"), 3000)
				.setMultiplierAtStart(0.1f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/ruined_portal", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/ruined_portal"), 2000)
				.setMultiplierAtStart(0.1f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/shipwreck_treasure", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/shipwreck_treasure"), 3000)
				.setMultiplierAtStart(0.1f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/simple_dungeon", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/simple_dungeon"), 2000)
				.setMultiplierAtStart(0.6f)
				.applyToDamageable()
				.build()
		);
		provider.add(path + "chests/spawn_bonus_chest", new LootPurgerModifier.Builder(new ResourceLocation("minecraft:chests/spawn_bonus_chest"), 2000)
				.setMultiplierAtStart(0.7f)
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

		/*for (WoodTypes woodTypes : WOOD_TYPES) {
			provider.add(path + "trees/" + woodTypes.name, new ReplaceLootModifier.Builder(new LootItemCondition[] {
							LootItemEntityPropertyCondition.hasProperties(
											LootContext.EntityTarget.THIS,
											new EntityPredicate.Builder().equipment(
															new EntityEquipmentPredicate.Builder().mainhand(
																			ItemPredicate.Builder.item().of(ItemTags.AXES)
																					.build())
																	.build())
													.build())
									.invert()
									.build()
					}, woodTypes.log, woodTypes.planks)
							.setMultipliers(List.of(2.0f))
							.build()
			);
		}*/
	}
}