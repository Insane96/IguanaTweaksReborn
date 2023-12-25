package insane96mcp.iguanatweaksreborn.module.experience;

import insane96mcp.iguanatweaksreborn.data.lootmodifier.InjectLootTableModifier;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.experience.anvils.Anvils;
import insane96mcp.iguanatweaksreborn.setup.SRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Label(name = "Lapis", description = "New lapis for better enchanting.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class Lapis extends Feature {

	public static final RegistryObject<Item> CLEANSED_LAPIS = SRRegistries.ITEMS.register("cleansed_lapis", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> ANCIENT_LAPIS = SRRegistries.ITEMS.register("ancient_lapis", () -> new Item(new Item.Properties().fireResistant()));

	public Lapis(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onAnvilUpdateAncient(final AnvilUpdateEvent event) {
		if (!this.isEnabled())
			return;

		ItemStack left = event.getLeft();
		if (!left.isEnchanted()
				&& !left.is(Items.ENCHANTED_BOOK))
			return;

		ItemStack right = event.getRight();
		boolean isAncient = right.is(ANCIENT_LAPIS.get());
		if (!isAncient && !right.is(CLEANSED_LAPIS.get()))
			return;

		Map<Enchantment, Integer> allEnchantments = EnchantmentHelper.getEnchantments(left);
		List<Enchantment> possibleEnchantments = new ArrayList<>();
		for (var ench : allEnchantments.entrySet()) {
			int lvl = ench.getValue();
			int maxLvl = ench.getKey().getMaxLevel();

			//Allow only one "upgrade" per item
			if (isAncient && lvl >= maxLvl + 1)
				return;

			//Don't accept enchantments with a max level of 1
			if (maxLvl <= 1)
				continue;
			//If is ancient lapis only accept max level enchantments
			if (isAncient && lvl < maxLvl)
				continue;
			//Else if is cleansed, only accept non-max level enchantments
			if (!isAncient && lvl >= maxLvl)
				continue;

			possibleEnchantments.add(ench.getKey());
		}
		if (possibleEnchantments.isEmpty())
			return;

		RandomSource random = event.getPlayer().getRandom();
		random.setSeed(event.getPlayer().getEnchantmentSeed());

		Enchantment enchantmentChosen = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));
		int enchantmentLvlChosen = left.getEnchantmentLevel(enchantmentChosen);

		ItemStack result = left.copy();
		result.removeTagKey(ItemStack.TAG_ENCH);
		result.removeTagKey(EnchantedBookItem.TAG_STORED_ENCHANTMENTS);
		for (var ench : allEnchantments.entrySet()) {
			if (ench.getKey().equals(enchantmentChosen))
				enchantStack(result, ench.getKey(), ench.getValue() + 1);
			else
				enchantStack(result, ench.getKey(), ench.getValue());
		}

		event.setCost(Anvils.getRarityCost(enchantmentChosen) * (enchantmentLvlChosen + 1));
		event.setMaterialCost(1);
		event.setOutput(result);
	}

	//Damn Enchanted Books
	public static void enchantStack(ItemStack stack, Enchantment enchantment, int level) {
		if (stack.is(Items.ENCHANTED_BOOK))
			EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(enchantment, level));
		else
			stack.enchant(enchantment, level);
	}

	private static final String path = "experience/lapis";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "blocks/lapis_ore", new InjectLootTableModifier(new ResourceLocation("minecraft:blocks/lapis_ore"), new ResourceLocation("iguanatweaksreborn:blocks/cleansed_lapis")));
		provider.add(path + "blocks/deepslate_lapis_ore", new InjectLootTableModifier(new ResourceLocation("minecraft:blocks/deepslate_lapis_ore"), new ResourceLocation("iguanatweaksreborn:blocks/cleansed_lapis")));
	}
}
