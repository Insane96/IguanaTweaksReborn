package insane96mcp.survivalreimagined.data;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.setup.SRRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SRUpgradeItemRecipe extends CustomRecipe {

    Ingredient itemRequired;
    int amountRequired;
    Ingredient itemToUpgrade;
    ItemStack result;
    boolean keepDamage;

    final String group;

    public SRUpgradeItemRecipe(ResourceLocation id, CraftingBookCategory category, String group, Ingredient itemRequired, int amountRequired, Ingredient itemToUpgrade, ItemStack result) {
        this(id, category, group, itemRequired, amountRequired, itemToUpgrade, result, true);
    }

    public SRUpgradeItemRecipe(ResourceLocation id, CraftingBookCategory category, String group, Ingredient itemRequired, int amountRequired, Ingredient itemToUpgrade, ItemStack result, boolean keepDamage) {
        super(id, category);
        this.group = group;

        this.itemRequired = itemRequired;
        this.amountRequired = amountRequired;
        this.itemToUpgrade = itemToUpgrade;
        this.result = result;
        this.keepDamage = keepDamage;
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, @NotNull Level level) {
        int amountRequiredFound = 0;
        boolean itemToUpgradeFound = false;

        int i = 0;
        for(int j = 0; j < craftingContainer.getContainerSize(); ++j) {
            if (!craftingContainer.getItem(j).isEmpty())
                i++;
            ItemStack stack = craftingContainer.getItem(j);
            if (this.itemRequired.test(stack)) {
                amountRequiredFound++;
            }
            else if (this.itemToUpgrade.test(stack)) {
                itemToUpgradeFound = true;
            }
        }

        return i == expectedItems() && this.amountRequired == amountRequiredFound && itemToUpgradeFound;
    }

    private int expectedItems() {
        return amountRequired + 1;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        Ingredient[] itemsRequired = new Ingredient[this.amountRequired + 1];
        itemsRequired[0] = this.itemToUpgrade;
        for (int i = 1; i <= this.amountRequired; i++) {
            itemsRequired[i] = itemRequired;
        }
        return NonNullList.of(Ingredient.EMPTY, itemsRequired);
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack stackToUpgrade = null;
        for(int j = 0; j < craftingContainer.getContainerSize(); ++j) {
            ItemStack stack = craftingContainer.getItem(j);
            if (this.itemToUpgrade.test(stack)) {
                stackToUpgrade = stack;
                break;
            }
        }

        ItemStack result = this.getResultItem().copy();
        result.setDamageValue(stackToUpgrade.getDamageValue());
        return result;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return this.amountRequired <= 8;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SRRecipeSerializers.UPGRADE_ITEM_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<SRUpgradeItemRecipe> {

        @Override
        public SRUpgradeItemRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            String group = GsonHelper.getAsString(jsonObject, "group", "");
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", null), CraftingBookCategory.EQUIPMENT);
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            int amount = GsonHelper.getAsInt(jsonObject, "amount");
            Ingredient toUpgrade = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "to_upgrade"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            boolean keepDurability = GsonHelper.getAsBoolean(jsonObject, "keep_durability");
            return new SRUpgradeItemRecipe(id, category, group, ingredient, amount, toUpgrade, result, keepDurability);
        }

        @Override
        public @Nullable SRUpgradeItemRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf byteBuf) {
            String group = byteBuf.readUtf();
            CraftingBookCategory category = byteBuf.readEnum(CraftingBookCategory.class);
            Ingredient ingredient = Ingredient.fromNetwork(byteBuf);
            int amount = byteBuf.readInt();
            Ingredient toUpgrade = Ingredient.fromNetwork(byteBuf);
            ItemStack result = byteBuf.readItem();
            boolean keepDurability = byteBuf.readBoolean();
            return new SRUpgradeItemRecipe(id, category, group, ingredient, amount, toUpgrade, result, keepDurability);
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, SRUpgradeItemRecipe upgradeItemRecipe) {
            byteBuf.writeUtf(upgradeItemRecipe.group);
            byteBuf.writeEnum(upgradeItemRecipe.category());
            upgradeItemRecipe.itemRequired.toNetwork(byteBuf);
            byteBuf.writeInt(upgradeItemRecipe.amountRequired);
            upgradeItemRecipe.itemToUpgrade.toNetwork(byteBuf);
            byteBuf.writeItem(upgradeItemRecipe.result);
            byteBuf.writeBoolean(upgradeItemRecipe.keepDamage);
        }
    }
}
