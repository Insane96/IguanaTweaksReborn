package insane96mcp.survivalreimagined.module.mining.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultiItemSmeltingRecipe implements Recipe<Container> {
    protected final RecipeType<?> type;
    protected final ResourceLocation id;
    private final CookingBookCategory category;
    protected final String group;
    final NonNullList<Ingredient> ingredients;
    final float experience;
    final float doubleOutputChance;
    private final ItemStack result;
    protected final int cookingTime;
    @Nullable
    final Recycle recycle;

    private static final RandomSource RANDOM = RandomSource.create();

    public AbstractMultiItemSmeltingRecipe(RecipeType<?> type, ResourceLocation pId, String pGroup, CookingBookCategory pCategory, int maxIngredients, NonNullList<Ingredient> ingredients, ItemStack pResult, float doubleOutputChance, float pExperience, int pCookingTime, @Nullable Recycle recycle) {
        this.type = type;
        this.category = pCategory;
        this.id = pId;
        this.group = pGroup;
        this.ingredients = NonNullList.withSize(maxIngredients, Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            this.ingredients.set(i, ingredients.get(i));
        }
        this.result = pResult;
        this.doubleOutputChance = doubleOutputChance;
        this.experience = pExperience;
        this.cookingTime = pCookingTime;
        this.recycle = recycle;
    }

    @Override
    public boolean matches(Container container, Level pLevel) {
        List<Integer> checkedSlots = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            boolean ingredientMatches = false;
            for (int slot : getIngredientSlots()) {
                if (checkedSlots.contains(slot))
                    continue;
                if (ingredient.test(container.getItem(slot))) {
                    checkedSlots.add(slot);
                    ingredientMatches = true;
                    break;
                }
            }
            if (!ingredientMatches)
                return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack stack = this.result.copy();
        if (this.recycle != null) {
            ItemStack containerStack;
            for (int slot : getIngredientSlots()) {
                containerStack = container.getItem(slot);
                if (containerStack != ItemStack.EMPTY) {
                    stack.setCount((int) (Utils.getPercentageDurabilityLeft(containerStack) * this.recycle.amountAtFullDurability * this.recycle.ratio));
                    break;
                }
            }
        }
        else if (RANDOM.nextFloat() < doubleOutputChance) {
            stack.setCount(stack.getCount() + 1);
        }
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.addAll(this.ingredients);
        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MultiBlockFurnaces.BLASTING_RECIPE_SERIALIZER.get();
    }

    public float getDoubleOutputChance() {
        return this.doubleOutputChance;
    }
    /**
     * Gets the experience of this recipe
     */
    public float getExperience() {
        return this.experience;
    }

    /**
     * Recipes with equal group are combined into one button in the recipe book
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * Gets the cook time in ticks
     */
    public int getCookingTime() {
        return this.cookingTime;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeType<?> getType() {
        return this.type;
    }

    @Nullable
    public Recycle getRecycle() {
        return this.recycle;
    }

    public CookingBookCategory category() {
        return this.category;
    }

    public record Recycle(@SerializedName("amount_at_full_durability") int amountAtFullDurability, float ratio) {
        public static Recycle fromJson(JsonObject object) {
            if (!object.has("amount_at_full_durability"))
                throw new JsonParseException("Too many ingredients for multi item smelting recipe. The maximum is 1 when recycling is present");
            int amountAtFullDurability = object.get("amount_at_full_durability").getAsInt();
            float ratio = 1f;
            if (object.has("ratio"))
                ratio = Mth.clamp(object.get("ratio").getAsFloat(), 0f, 1f);
            return new Recycle(amountAtFullDurability, ratio);
        }

        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("amount_at_full_durability", this.amountAtFullDurability);
            if (this.ratio != 1f)
                jsonObject.addProperty("ratio", this.ratio);
            return jsonObject;
        }

        @Nullable
        public static Recycle fromNetwork(FriendlyByteBuf pBuffer) {
            if (pBuffer.readBoolean())
                return new Recycle(pBuffer.readInt(), pBuffer.readFloat());
            return null;
        }

        public void toNetwork(FriendlyByteBuf pBuffer) {
            pBuffer.writeInt(this.amountAtFullDurability);
            pBuffer.writeFloat(this.ratio);
        }
    }

    abstract int[] getIngredientSlots();
}
