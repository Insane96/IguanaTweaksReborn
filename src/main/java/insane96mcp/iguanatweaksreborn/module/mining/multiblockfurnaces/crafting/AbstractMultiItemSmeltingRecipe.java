package insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import insane96mcp.iguanatweaksreborn.setup.client.SRBookCategory;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import insane96mcp.insanelib.util.MathHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultiItemSmeltingRecipe implements Recipe<Container> {
    protected final RecipeType<?> type;
    protected final ResourceLocation id;
    private final SRBookCategory category;
    protected final String group;
    final NonNullList<Ingredient> ingredients;
    final float experience;
    final float outputIncrease;
    private final ItemStack result;
    protected final int cookingTime;
    @Nullable
    final Recycle recycle;

    private static final RandomSource RANDOM = RandomSource.create();

    public AbstractMultiItemSmeltingRecipe(RecipeType<?> type, ResourceLocation pId, String pGroup, SRBookCategory pCategory, int maxIngredients, NonNullList<Ingredient> ingredients, ItemStack pResult, float outputIncrease, float pExperience, int pCookingTime, @Nullable Recycle recycle) {
        this.type = type;
        this.category = pCategory;
        this.id = pId;
        this.group = pGroup;
        this.ingredients = NonNullList.withSize(maxIngredients, Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            this.ingredients.set(i, ingredients.get(i));
        }
        this.result = pResult;
        this.outputIncrease = outputIncrease;
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

    public boolean hasIngredient(ItemStack stack, Level pLevel) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(stack))
                return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack stack = this.result.copy();
        if (this.recycle != null) {
            ItemStack containerStack;
            for (int slot : getIngredientSlots()) {
                containerStack = container.getItem(slot);
                if (containerStack != ItemStack.EMPTY) {
                    stack.setCount((int) (MCUtils.getPercentageDurabilityLeft(containerStack) * this.recycle.amountAtFullDurability * this.recycle.ratio));
                    break;
                }
            }
        }
        else if (this.outputIncrease > 0f) {
            int bonusAmount = MathHelper.getAmountWithDecimalChance(RANDOM, this.outputIncrease);
            stack.setCount(stack.getCount() + bonusAmount);
        }
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
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
        return null;
    }

    public float getOutputIncrease() {
        return this.outputIncrease;
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

    public SRBookCategory category() {
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

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = this.getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream().anyMatch(AbstractMultiItemSmeltingRecipe::hasNoElements);
    }

    public static boolean hasNoElements(Ingredient ingredient)
    {
        ItemStack[] items = ingredient.getItems();
        if (items.length == 1)
        {
            //If we potentially added a barrier due to the ingredient being an empty tag, try and check if it is the stack we added
            ItemStack item = items[0];
            return item.getItem() == Items.BARRIER && item.getHoverName() instanceof MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
        }
        return false;
    }
}
