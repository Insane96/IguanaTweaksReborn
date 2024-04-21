package insane96mcp.iguanatweaksreborn.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.experience.anvils.AnvilRepair;
import insane96mcp.iguanatweaksreborn.module.experience.anvils.AnvilRepairReloadListener;
import insane96mcp.iguanatweaksreborn.module.experience.anvils.Anvils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

@EmiEntrypoint
public class ITREmiPlugin implements EmiPlugin {

	@Override
	public void register(EmiRegistry registry) {
		RecipeManager manager = registry.getRecipeManager();

		if (Feature.isEnabled(Anvils.class)) {
			for (Map.Entry<ResourceLocation, AnvilRepair> anvilRepair : AnvilRepairReloadListener.REPAIRS.entrySet()) {
				for (ItemStack stack : anvilRepair.getValue().itemToRepair.getAllItemStacks()) {
					for (AnvilRepair.RepairData repairData : anvilRepair.getValue().repairData) {
						IdTagMatcher idTagMatcher = repairData.repairMaterial();
						if (idTagMatcher.type == IdTagMatcher.Type.ID)
							registry.addRecipe(new EmiAnvilRepairRecipe(anvilRepair.getKey(), stack, ForgeRegistries.ITEMS.getValue(idTagMatcher.location), repairData.amountRequired(), repairData.maxRepair()));
						else
							registry.addRecipe(new EmiAnvilRepairRecipe(anvilRepair.getKey(), stack, TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), idTagMatcher.location), repairData.amountRequired(), repairData.maxRepair()));

					}
				}
			}
			registry.removeRecipes(recipe -> recipe.getId() != null && "emi".equals(recipe.getId().getNamespace()) && recipe.getId().getPath().startsWith("/anvil/repairing/material"));
			registry.removeRecipes(recipe -> recipe.getId() != null && "emi".equals(recipe.getId().getNamespace()) && recipe.getId().getPath().startsWith("/anvil/enchanting"));
		}
	}

	public EmiInfoRecipe createSimpleInfo(Item item, String id, Component component) {
		return new EmiInfoRecipe(List.of(emiIngredientOf(item)), List.of(component), new ResourceLocation(IguanaTweaksReborn.MOD_ID, id));
	}

	public static EmiIngredient emiIngredientOf(Item item) {
		return EmiIngredient.of(Ingredient.of(item));
	}
}
