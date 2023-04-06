package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.data.lootmodifier.ReplaceDropModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Label(name = "Ancient Lapis", description = "Add an item that increases the level of an enchantment, up to 1 level above the limit.")
@LoadFeature(module = Modules.Ids.ITEMS)
public class AncientLapis extends Feature {
	public static final RegistryObject<Item> ANCIENT_LAPIS = SRItems.ITEMS.register("ancient_lapis", () -> new Item(new Item.Properties().stacksTo(1).fireResistant()));

	public AncientLapis(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void anvilUpdateEvent(final AnvilUpdateEvent event) {
		if (!this.isEnabled())
			return;

		ItemStack left = event.getLeft();
		if (!left.isEnchanted())
			return;

		ItemStack right = event.getRight();
		if (!right.is(ANCIENT_LAPIS.get()))
			return;

		Map<Enchantment, Integer> allEnchantments = left.getAllEnchantments();
		List<Enchantment> possibleEnchantments = new ArrayList<>();
		for (var ench : allEnchantments.entrySet()) {
			if (ench.getKey().getMaxLevel() <= 1
					|| ench.getValue() >= ench.getKey().getMaxLevel() + 1)
				continue;

			possibleEnchantments.add(ench.getKey());
		}
		if (possibleEnchantments.isEmpty())
			return;

		RandomSource random = event.getPlayer().getRandom();
		random.setSeed(event.getPlayer().getEnchantmentSeed());

		Enchantment enchantmentChosen = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));

		ItemStack result = left.copy();
		result.removeTagKey("Enchantments");
		for (var ench : allEnchantments.entrySet()) {
			if (ench.getKey().equals(enchantmentChosen))
				result.enchant(ench.getKey(), ench.getValue() + 1);
			else
				result.enchant(ench.getKey(), ench.getValue());
		}
		if (result.getBaseRepairCost() < 25)
			result.setRepairCost(25);

		event.setCost(25);
		event.setMaterialCost(1);
		event.setOutput(result);
	}

	private static final String path = "ancient_lapis/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "ancient_lapis_from_deepslate_ore", new ReplaceDropModifier.Builder(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.DEEPSLATE_LAPIS_ORE).build()},
				Items.LAPIS_LAZULI,
				ANCIENT_LAPIS.get())
				.setAmountToReplace(1)
				.setChances(List.of(0.12f, 0.135f, 0.155f, 0.17f, 0.20f))
				.build());
	}
}