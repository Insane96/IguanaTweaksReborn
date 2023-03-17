package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.SRUpgradeItemRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SRRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<RecipeSerializer<SRUpgradeItemRecipe>> UPGRADE_ITEM_RECIPE = RECIPE_SERIALIZERS.register("upgrade_item", SRUpgradeItemRecipe.Serializer::new);
}
