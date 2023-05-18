package insane96mcp.survivalreimagined.module.mining.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class GhostRecipeAmount {
    @Nullable
    private Recipe<?> recipe;
    private final List<GhostRecipeAmount.GhostIngredient> ingredients = Lists.newArrayList();
    float time;

    public void clear() {
        this.recipe = null;
        this.ingredients.clear();
        this.time = 0.0F;
    }

    public void addIngredient(Ingredient pIngredient, int amount, int pX, int pY) {
        this.ingredients.add(new GhostRecipeAmount.GhostIngredient(pIngredient, amount, pX, pY));
    }

    public GhostRecipeAmount.GhostIngredient get(int pIndex) {
        return this.ingredients.get(pIndex);
    }

    public int size() {
        return this.ingredients.size();
    }

    @Nullable
    public Recipe<?> getRecipe() {
        return this.recipe;
    }

    public void setRecipe(Recipe<?> pRecipe) {
        this.recipe = pRecipe;
    }

    public void render(PoseStack pPoseStack, Minecraft pMinecraft, int pLeftPos, int pTopPos, boolean p_100154_, float pPartialTick) {
        if (!Screen.hasControlDown()) {
            this.time += pPartialTick;
        }

        for(int i = 0; i < this.ingredients.size(); ++i) {
            GhostRecipeAmount.GhostIngredient ghostrecipe$ghostingredient = this.ingredients.get(i);
            int j = ghostrecipe$ghostingredient.getX() + pLeftPos;
            int k = ghostrecipe$ghostingredient.getY() + pTopPos;
            if (i == 0 && p_100154_) {
                GuiComponent.fill(pPoseStack, j - 4, k - 4, j + 20, k + 20, 822018048);
            } else {
                GuiComponent.fill(pPoseStack, j, k, j + 16, k + 16, 822018048);
            }

            ItemStack itemstack = ghostrecipe$ghostingredient.getItem();
            ItemRenderer itemrenderer = pMinecraft.getItemRenderer();
            itemrenderer.renderAndDecorateFakeItem(pPoseStack, itemstack, j, k);
            RenderSystem.depthFunc(516);
            GuiComponent.fill(pPoseStack, j, k, j + 16, k + 16, 822083583);
            RenderSystem.depthFunc(515);
            if (i == 0) {
                itemrenderer.renderGuiItemDecorations(pPoseStack, pMinecraft.font, itemstack, j, k);
            }
            if (itemstack.getCount() != 1)
                itemrenderer.renderGuiItemDecorations(pPoseStack, pMinecraft.font, itemstack, j, k, String.valueOf(itemstack.getCount()));
        }

    }

    @OnlyIn(Dist.CLIENT)
    public class GhostIngredient {
        private final Ingredient ingredient;
        private final int amount;
        private final int x;
        private final int y;

        public GhostIngredient(Ingredient pIngredient, int amount, int pX, int pY) {
            this.ingredient = pIngredient;
            this.amount = amount;
            this.x = pX;
            this.y = pY;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public ItemStack getItem() {
            ItemStack[] aitemstack = this.ingredient.getItems();
            ItemStack stack = aitemstack.length == 0 ? ItemStack.EMPTY : aitemstack[Mth.floor(GhostRecipeAmount.this.time / 30.0F) % aitemstack.length];
            stack.setCount(this.amount);
            return stack;
        }
    }
}
