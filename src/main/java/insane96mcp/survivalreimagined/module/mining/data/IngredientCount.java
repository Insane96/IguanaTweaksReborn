package insane96mcp.survivalreimagined.module.mining.data;

import com.google.gson.JsonElement;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class IngredientWithCount extends AbstractIngredient {

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return null;
    }

    @Override
    public JsonElement toJson() {
        return null;
    }

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        if (pStack == null) {
            return false;
        }
        else if (this.isEmpty()) {
            return pStack.isEmpty();
        }
        else {
            for(ItemStack itemstack : this.getItems()) {
                if (itemstack.is(pStack.getItem()) && pStack.getCount() >= itemstack.getCount()) {
                    return true;
                }
            }

            return false;
        }
    }
}
