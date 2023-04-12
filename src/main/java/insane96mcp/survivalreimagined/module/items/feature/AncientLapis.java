package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
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
	public void anvilUpdateLapis(final AnvilUpdateEvent event) {
		if (!this.isEnabled())
			return;

		ItemStack left = event.getLeft();
		if (!left.isEnchanted()
				&& !left.is(Items.ENCHANTED_BOOK))
			return;

		ItemStack right = event.getRight();
		if (!right.is(ANCIENT_LAPIS.get()))
			return;

		Map<Enchantment, Integer> allEnchantments = EnchantmentHelper.getEnchantments(left);
		List<Enchantment> possibleEnchantments = new ArrayList<>();
		for (var ench : allEnchantments.entrySet()) {
			//Allow only one "upgrade" per item
			if (ench.getValue() >= ench.getKey().getMaxLevel() + 1)
				return;

			if (ench.getKey().getMaxLevel() <= 1)
				continue;

			possibleEnchantments.add(ench.getKey());
		}
		if (possibleEnchantments.isEmpty())
			return;

		RandomSource random = event.getPlayer().getRandom();
		random.setSeed(event.getPlayer().getEnchantmentSeed());

		Enchantment enchantmentChosen = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));

		ItemStack result = left.copy();
		result.removeTagKey(ItemStack.TAG_ENCH);
		result.removeTagKey(EnchantedBookItem.TAG_STORED_ENCHANTMENTS);
		for (var ench : allEnchantments.entrySet()) {
			if (ench.getKey().equals(enchantmentChosen))
				enchantStack(result, ench.getKey(), ench.getValue() + 1);
			else
				enchantStack(result, ench.getKey(), ench.getValue());
		}
		if (result.getBaseRepairCost() < 25)
			result.setRepairCost(25);

		event.setCost(25);
		event.setMaterialCost(1);
		event.setOutput(result);
	}

	public static void enchantStack(ItemStack stack, Enchantment enchantment, int level) {
		if (stack.is(Items.ENCHANTED_BOOK))
			EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(enchantment, level));
		else
			stack.enchant(enchantment, level);
	}
}