package insane96mcp.survivalreimagined.module.mining.block;

import com.google.common.collect.Lists;
import insane96mcp.survivalreimagined.module.mining.crafting.ForgeRecipe;
import insane96mcp.survivalreimagined.module.mining.inventory.ForgeMenu;
import insane96mcp.survivalreimagined.module.mining.inventory.MultiBlockBlastFurnaceMenu;
import insane96mcp.survivalreimagined.module.mining.inventory.MultiBlockSoulBlastFurnaceMenu;
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
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForgeBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
    public static final int DATA_SMASHES_REQUIRED = 0;
    public static final int DATA_SMASHES = 1;
    protected NonNullList<ItemStack> items = NonNullList.withSize(ForgeMenu.SLOT_COUNT, ItemStack.EMPTY);
    int smashesRequired;
    int smashes;

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int dataId) {
            return switch (dataId) {
                case DATA_SMASHES_REQUIRED -> ForgeBlockEntity.this.smashesRequired;
                case DATA_SMASHES -> ForgeBlockEntity.this.smashes;
                default -> 0;
            };
        }

        public void set(int dataId, int data) {
            switch (dataId) {
                case DATA_SMASHES_REQUIRED -> ForgeBlockEntity.this.smashesRequired = data;
                case DATA_SMASHES -> ForgeBlockEntity.this.smashes = data;
            }

        }

        public int getCount() {
            return ForgeMenu.DATA_COUNT;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<Container, ? extends ForgeRecipe> quickCheck;

    protected ForgeBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, RecipeType<? extends ForgeRecipe> pRecipeType) {
        super(pType, pPos, pBlockState);
        this.quickCheck = RecipeManager.createCheck((RecipeType)pRecipeType);
        this.recipeType = pRecipeType;
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
        this.smashesRequired = pTag.getInt("SmashesRequired");
        this.smashes = pTag.getInt("Smashes");
        CompoundTag compoundtag = pTag.getCompound("RecipesUsed");

        for(String s : compoundtag.getAllKeys()) {
            this.recipesUsed.put(new ResourceLocation(s), compoundtag.getInt(s));
        }
    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("SmashesRequired", this.smashesRequired);
        pTag.putInt("Smashes", this.smashes);
        ContainerHelper.saveAllItems(pTag, this.items);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((p_187449_, p_187450_) -> compoundtag.putInt(p_187449_.toString(), p_187450_));
        pTag.put("RecipesUsed", compoundtag);
    }

    public static void onUse(Level pLevel, BlockPos pPos, BlockState pState, ForgeBlockEntity pBlockEntity) {

    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, ForgeBlockEntity pBlockEntity) {
        boolean setChanged = false;

        ItemStack resultStack = pBlockEntity.items.get(ForgeMenu.RESULT_SLOT);
        if (!resultStack.isEmpty())
            return;

        ItemStack ingredientStack = pBlockEntity.items.get(ForgeMenu.INGREDIENT_SLOT);
        ItemStack gearStack = pBlockEntity.items.get(ForgeMenu.GEAR_SLOT);
        if (ingredientStack.isEmpty() || gearStack.isEmpty()) {
            pBlockEntity.smashes = 0;
            return;
        }

        Recipe<?> recipe = pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).orElse(null);

        int maxStackSize = pBlockEntity.getMaxStackSize();
        if (pBlockEntity.canForge(pLevel.registryAccess(), recipe, pBlockEntity.items, maxStackSize)) {
            ++pBlockEntity.smashes;
            if (pBlockEntity.smashes == pBlockEntity.smashesRequired) {
                pBlockEntity.smashes = 0;
                pBlockEntity.smashesRequired = getSmashesRequired(pLevel, pBlockEntity);
                if (pBlockEntity.forge(pLevel.registryAccess(), recipe, pBlockEntity.items, maxStackSize)) {
                    pBlockEntity.setRecipeUsed(recipe);
                }

                setChanged = true;
            }
        }
        else {
            pBlockEntity.smashes = 0;
        }

        if (setChanged)
            setChanged(pLevel, pPos, pState);
    }

    private boolean canForge(RegistryAccess registryAccess, @javax.annotation.Nullable Recipe<?> recipe, NonNullList<ItemStack> slotsStacks, int stackSize) {
        if (recipe == null)
            return false;

        boolean hasIngredient = !slotsStacks.get(ForgeMenu.INGREDIENT_SLOT).isEmpty();
        boolean hasGear = !slotsStacks.get(ForgeMenu.GEAR_SLOT).isEmpty();
        if (!hasIngredient || !hasGear)
            return false;

        ItemStack resultStack = ((Recipe<WorldlyContainer>) recipe).assemble(this, registryAccess);
        if (resultStack.isEmpty()) {
            return false;
        }
        else {
            ItemStack resultSlotStack = slotsStacks.get(ForgeMenu.RESULT_SLOT);
            if (resultSlotStack.isEmpty())
                return true;
            else if (!resultSlotStack.sameItem(resultStack))
                return false;
            else if (resultSlotStack.getCount() >= resultStack.getMaxStackSize())
                return false;
            return resultSlotStack.getCount() + resultStack.getCount() < stackSize;
        }
    }

    private boolean forge(RegistryAccess registryAccess, @javax.annotation.Nullable Recipe<?> recipe, NonNullList<ItemStack> slotStacks, int stackSize) {
        if (!this.canForge(registryAccess, recipe, slotStacks, stackSize))
            return false;
        ItemStack resultStack = ((Recipe<WorldlyContainer>) recipe).assemble(this, registryAccess);
        ItemStack resultSlotStack = slotStacks.get(ForgeMenu.RESULT_SLOT);
        if (resultSlotStack.isEmpty()) {
            slotStacks.set(ForgeMenu.RESULT_SLOT, resultStack.copy());
        }
        else if (resultSlotStack.is(resultStack.getItem())) {
            resultSlotStack.grow(resultStack.getCount());
        }

        slotStacks.get(ForgeMenu.INGREDIENT_SLOT).shrink(((ForgeRecipe)recipe).getIngredientAmount());
        slotStacks.get(ForgeMenu.GEAR_SLOT).shrink(1);
        return true;
    }

    private static int getSmashesRequired(Level pLevel, ForgeBlockEntity pBlockEntity) {
        return pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).map(ForgeRecipe::getSmashesRequired).orElse(10);
    }

    private static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
        if (!stack1.is(stack2.getItem())
                || stack1.getDamageValue() != stack2.getDamageValue()) {
            return false;
        }
        else {
            return stack1.getCount() <= stack1.getMaxStackSize() && ItemStack.tagMatches(stack1, stack2);
        }
    }

    //TODO Allow automatic forging
    @Override
    public int[] getSlotsForFace(Direction pSide) {
        if (pSide == Direction.DOWN) {
            return ForgeMenu.RESULT_SLOT;
        } else {
            if (pSide != Direction.UP) {
                return SLOTS_FOR_SIDES;
            }

            if (this instanceof MultiBlockBlastFurnaceBlockEntity)
                return MultiBlockBlastFurnaceMenu.getIngredientSlots();
            else if (this instanceof MultiBlockSoulBlastFurnaceBlockEntity)
                return MultiBlockSoulBlastFurnaceMenu.getIngredientSlots();
        }
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return this.canPlaceItem(pIndex, pItemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return true;
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

        if (pSlot < ForgeMenu.RESULT_SLOT && !flag) {
            this.smashesRequired = getSmashesRequired(this.level, this);
            this.smashes = 0;
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

    public List<Recipe<?>> getRecipesToAward(ServerLevel pLevel, Vec3 pPopVec) {
        List<Recipe<?>> list = Lists.newArrayList();

        for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent(list::add);
        }

        return list;
    }

    @Override
    public void fillStackedContents(StackedContents pHelper) {
        for(ItemStack itemstack : this.items) {
            pHelper.accountStack(itemstack);
        }
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
