package insane96mcp.survivalreimagined.module.mining.block;

import com.google.common.collect.Lists;
import insane96mcp.survivalreimagined.module.mining.crafting.MultiItemSmeltingRecipe;
import insane96mcp.survivalreimagined.module.mining.inventory.AbstractMultiBlockFurnaceMenu;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static insane96mcp.survivalreimagined.module.mining.inventory.AbstractMultiBlockFurnaceMenu.*;

public abstract class AbstractMultiBlockFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {

    private static final int[] SLOTS_FOR_UP = new int[] {INGREDIENT_SLOTS[0]};
    private static final int[] SLOTS_FOR_DOWN = new int[]{AbstractMultiBlockFurnaceMenu.RESULT_SLOT, FUEL_SLOT};
    private static final int[] SLOTS_FOR_SIDES = new int[]{FUEL_SLOT};
    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 4;
    private final RecipeType<? extends MultiItemSmeltingRecipe> recipeType;
    protected NonNullList<ItemStack> items = NonNullList.withSize(AbstractMultiBlockFurnaceMenu.SLOT_COUNT, ItemStack.EMPTY);
    int litTime;
    int litDuration;
    int cookingProgress;
    int cookingTotalTime;

    boolean isValid = false;

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int dataId) {
            return switch (dataId) {
                case DATA_LIT_TIME -> AbstractMultiBlockFurnaceBlockEntity.this.litTime;
                case DATA_LIT_DURATION -> AbstractMultiBlockFurnaceBlockEntity.this.litDuration;
                case DATA_COOKING_PROGRESS -> AbstractMultiBlockFurnaceBlockEntity.this.cookingProgress;
                case DATA_COOKING_TOTAL_TIME -> AbstractMultiBlockFurnaceBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        public void set(int dataId, int data) {
            switch (dataId) {
                case DATA_LIT_TIME -> AbstractMultiBlockFurnaceBlockEntity.this.litTime = data;
                case DATA_LIT_DURATION -> AbstractMultiBlockFurnaceBlockEntity.this.litDuration = data;
                case DATA_COOKING_PROGRESS -> AbstractMultiBlockFurnaceBlockEntity.this.cookingProgress = data;
                case DATA_COOKING_TOTAL_TIME -> AbstractMultiBlockFurnaceBlockEntity.this.cookingTotalTime = data;
            }

        }

        public int getCount() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<Container, ? extends MultiItemSmeltingRecipe> quickCheck;

    protected AbstractMultiBlockFurnaceBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, RecipeType<? extends MultiItemSmeltingRecipe> pRecipeType) {
        super(pType, pPos, pBlockState);
        this.quickCheck = RecipeManager.createCheck((RecipeType)pRecipeType);
        this.recipeType = pRecipeType;
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
        this.litTime = pTag.getInt("BurnTime");
        this.cookingProgress = pTag.getInt("CookTime");
        this.cookingTotalTime = pTag.getInt("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.get(1));
        CompoundTag compoundtag = pTag.getCompound("RecipesUsed");

        for(String s : compoundtag.getAllKeys()) {
            this.recipesUsed.put(new ResourceLocation(s), compoundtag.getInt(s));
        }

    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("BurnTime", this.litTime);
        pTag.putInt("CookTime", this.cookingProgress);
        pTag.putInt("CookTimeTotal", this.cookingTotalTime);
        ContainerHelper.saveAllItems(pTag, this.items);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((p_187449_, p_187450_) -> {
            compoundtag.putInt(p_187449_.toString(), p_187450_);
        });
        pTag.put("RecipesUsed", compoundtag);
    }



    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, AbstractMultiBlockFurnaceBlockEntity pBlockEntity) {
        if (pLevel.getGameTime() % 20 == 11) {
            pBlockEntity.isValid = ((AbstractMultiBlockFurnace)pState.getBlock()).isValidMultiBlock(pLevel, pPos);
        }
        if (!pBlockEntity.isValid)
            return;
        boolean flag = pBlockEntity.isLit();
        boolean flag1 = false;
        if (pBlockEntity.isLit()) {
            --pBlockEntity.litTime;
        }

        ItemStack fuelStack = pBlockEntity.items.get(FUEL_SLOT);
        boolean hasInputItem = !pBlockEntity.items.get(0).isEmpty();
        for (int slot : INGREDIENT_SLOTS) {
            if (!pBlockEntity.items.get(slot).isEmpty()) {
                hasInputItem = true;
                break;
            }
        }
        boolean hasFuel = !fuelStack.isEmpty();
        if (pBlockEntity.isLit() || hasFuel && hasInputItem) {
            Recipe<?> recipe;
            if (hasInputItem) {
                recipe = pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).orElse(null);
            } else {
                recipe = null;
            }

            int i = pBlockEntity.getMaxStackSize();
            if (!pBlockEntity.isLit() && pBlockEntity.canBurn(pLevel.registryAccess(), recipe, pBlockEntity.items, i)) {
                pBlockEntity.litTime = pBlockEntity.getBurnDuration(fuelStack);
                pBlockEntity.litDuration = pBlockEntity.litTime;
                if (pBlockEntity.isLit()) {
                    flag1 = true;
                    if (fuelStack.hasCraftingRemainingItem())
                        pBlockEntity.items.set(FUEL_SLOT, fuelStack.getCraftingRemainingItem());
                    else
                    if (hasFuel) {
                        fuelStack.shrink(1);
                        if (fuelStack.isEmpty()) {
                            pBlockEntity.items.set(FUEL_SLOT, fuelStack.getCraftingRemainingItem());
                        }
                    }
                }
            }

            if (pBlockEntity.isLit() && pBlockEntity.canBurn(pLevel.registryAccess(), recipe, pBlockEntity.items, i)) {
                ++pBlockEntity.cookingProgress;
                if (pBlockEntity.cookingProgress == pBlockEntity.cookingTotalTime) {
                    pBlockEntity.cookingProgress = 0;
                    pBlockEntity.cookingTotalTime = getTotalCookTime(pLevel, pBlockEntity);
                    if (pBlockEntity.burn(pLevel.registryAccess(), recipe, pBlockEntity.items, i)) {
                        pBlockEntity.setRecipeUsed(recipe);
                    }

                    flag1 = true;
                }
            } else {
                pBlockEntity.cookingProgress = 0;
            }
        } else if (!pBlockEntity.isLit() && pBlockEntity.cookingProgress > 0) {
            pBlockEntity.cookingProgress = Mth.clamp(pBlockEntity.cookingProgress - 2, 0, pBlockEntity.cookingTotalTime);
        }

        if (flag != pBlockEntity.isLit()) {
            flag1 = true;
            pState = pState.setValue(AbstractFurnaceBlock.LIT, pBlockEntity.isLit());
            pLevel.setBlock(pPos, pState, 3);
        }

        if (flag1) {
            setChanged(pLevel, pPos, pState);
        }

    }

    private boolean canBurn(RegistryAccess registryAccess, @javax.annotation.Nullable Recipe<?> recipe, NonNullList<ItemStack> slotsStacks, int stackSize) {
        boolean hasIngredient = false;
        for (int slot : AbstractMultiBlockFurnaceMenu.INGREDIENT_SLOTS) {
            if (!slotsStacks.get(slot).isEmpty())
                hasIngredient = true;
        }
        if (!hasIngredient
                || recipe == null)
            return false;

        ItemStack resultStack = ((Recipe<WorldlyContainer>) recipe).assemble(this, registryAccess);
        if (resultStack.isEmpty()) {
            return false;
        }
        else {
            ItemStack resultSlotStack = slotsStacks.get(AbstractMultiBlockFurnaceMenu.RESULT_SLOT);
            if (resultSlotStack.isEmpty())
                return true;
            else if (!resultSlotStack.sameItem(resultStack))
                return false;
            else if (resultSlotStack.getCount() + resultStack.getCount() <= stackSize && resultSlotStack.getCount() + resultStack.getCount() <= resultSlotStack.getMaxStackSize()) // Forge fix: make furnace respect stack sizes in furnace recipes
                return true;
            else
                return resultSlotStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
        }
    }

    private boolean burn(RegistryAccess registryAccess, @javax.annotation.Nullable Recipe<?> recipe, NonNullList<ItemStack> slotStacks, int stackSize) {
        if (recipe == null
                || !this.canBurn(registryAccess, recipe, slotStacks, stackSize))
            return false;
        ItemStack itemstack1 = ((Recipe<WorldlyContainer>) recipe).assemble(this, registryAccess);
        ItemStack itemstack2 = slotStacks.get(RESULT_SLOT);
        if (itemstack2.isEmpty()) {
            slotStacks.set(RESULT_SLOT, itemstack1.copy());
        }
        else if (itemstack2.is(itemstack1.getItem())) {
            itemstack2.grow(itemstack1.getCount());
        }

        for (int slot : INGREDIENT_SLOTS) {
            slotStacks.get(slot).shrink(1);
        }
        return true;
    }

    protected int getBurnDuration(ItemStack pFuel) {
        return pFuel.isEmpty() ? 0 : net.minecraftforge.common.ForgeHooks.getBurnTime(pFuel, this.recipeType);
    }

    private static int getTotalCookTime(Level pLevel, AbstractMultiBlockFurnaceBlockEntity pBlockEntity) {
        return pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).map(MultiItemSmeltingRecipe::getCookingTime).orElse(BURN_TIME_STANDARD);
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        if (pSide == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return pSide == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return this.canPlaceItem(pIndex, pItemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        if (pDirection == Direction.DOWN && pIndex == 1) {
            return pStack.is(Items.WATER_BUCKET) || pStack.is(Items.BUCKET);
        } else {
            return true;
        }
    }

    @Override
    protected Component getDefaultName() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemstack = this.items.get(pSlot);
        boolean flag = !pStack.isEmpty() && pStack.sameItem(itemstack) && ItemStack.tagMatches(pStack, itemstack);
        this.items.set(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        if (pSlot == 0 && !flag) {
            this.cookingTotalTime = getTotalCookTime(this.level, this);
            this.cookingProgress = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {
        if (pRecipe != null) {
            ResourceLocation resourcelocation = pRecipe.getId();
            this.recipesUsed.addTo(resourcelocation, 1);
        }
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer pPlayer) {
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(pPlayer.getLevel(), pPlayer.position());
        pPlayer.awardRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec) {
        List<Recipe<?>> list = Lists.newArrayList();

        for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((p_155023_) -> {
                list.add(p_155023_);
                createExperience(pLevel, pPopVec, entry.getIntValue(), ((AbstractCookingRecipe)p_155023_).getExperience());
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel pLevel, Vec3 pPopVec, int pRecipeIndex, float pExperience) {
        int i = Mth.floor((float)pRecipeIndex * pExperience);
        float f = Mth.frac((float)pRecipeIndex * pExperience);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        ExperienceOrb.award(pLevel, pPopVec, i);
    }

    @Override
    public void fillStackedContents(StackedContents pHelper) {
        for(ItemStack itemstack : this.items) {
            pHelper.accountStack(itemstack);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 128;
    }

    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable Direction facing) {
        if (!this.remove && facing != null && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            else if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler> handler : handlers)
            handler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    }
}
