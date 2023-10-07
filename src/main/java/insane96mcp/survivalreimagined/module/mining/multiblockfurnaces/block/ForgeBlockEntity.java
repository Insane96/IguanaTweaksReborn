package insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.block;

import com.google.common.collect.Lists;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.forging.ForgeMenu;
import insane96mcp.survivalreimagined.module.mining.forging.ForgeRecipe;
import insane96mcp.survivalreimagined.module.mining.forging.Forging;
import insane96mcp.survivalreimagined.network.message.SyncForgeStatus;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForgeBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
    private static final int[] SLOTS_FOR_UP = new int[]{ForgeMenu.INGREDIENT_SLOT};
    private static final int[] SLOTS_FOR_DOWN = new int[]{ForgeMenu.RESULT_SLOT};
    private static final int[] SLOTS_FOR_SIDES = new int[]{ForgeMenu.GEAR_SLOT};
    public static final int DATA_SMASHES_REQUIRED = 0;
    public static final int DATA_SMASHES = 1;
    protected NonNullList<ItemStack> items = NonNullList.withSize(ForgeMenu.SLOT_COUNT, ItemStack.EMPTY);
    public int smashesRequired;
    public int smashes;

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

    public ForgeBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Forging.FORGE_BLOCK_ENTITY_TYPE.get(), pPos, pBlockState);
        //noinspection unchecked,rawtypes
        this.quickCheck = RecipeManager.createCheck((RecipeType)Forging.FORGE_RECIPE_TYPE.get());
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

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public static boolean onUse(Level pLevel, BlockPos pPos, BlockState pState, ForgeBlockEntity pBlockEntity, int smashes) {
        ItemStack resultStack = pBlockEntity.items.get(ForgeMenu.RESULT_SLOT);
        if (!resultStack.isEmpty())
            return false;

        ItemStack ingredientStack = pBlockEntity.items.get(ForgeMenu.INGREDIENT_SLOT);
        ItemStack gearStack = pBlockEntity.items.get(ForgeMenu.GEAR_SLOT);
        if (ingredientStack.isEmpty() || gearStack.isEmpty()) {
            pBlockEntity.smashes = 0;
            return false;
        }

        Recipe<?> recipe = pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).orElse(null);

        int maxStackSize = pBlockEntity.getMaxStackSize();
        if (pBlockEntity.canForge(pLevel.registryAccess(), recipe, pBlockEntity.items, maxStackSize)) {
            pBlockEntity.smashes += smashes;
            if (pBlockEntity.smashes >= pBlockEntity.smashesRequired) {
                pBlockEntity.smashes = 0;
                pBlockEntity.smashesRequired = getSmashesRequired(pLevel, pBlockEntity);
                if (pBlockEntity.forge(pLevel.registryAccess(), recipe, pBlockEntity.items, maxStackSize)) {
                    pBlockEntity.setRecipeUsed(recipe);
                }
                pLevel.playSound(null, pPos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 0.8f);

                setChanged(pLevel, pPos, pState);
            }
            else {
                pLevel.playSound(null, pPos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5f, 1.7f);
            }

            if (pLevel instanceof ServerLevel serverLevel)
                SyncForgeStatus.sync(serverLevel, pBlockEntity.getBlockPos(), pBlockEntity);
            return true;
        }
        else {
            pBlockEntity.smashes = 0;

            if (pLevel instanceof ServerLevel serverLevel)
                SyncForgeStatus.sync(serverLevel, pBlockEntity.getBlockPos(), pBlockEntity);
            return false;
        }
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
            else if (!ItemStack.isSameItemSameTags(resultSlotStack, resultStack))
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
        ItemStack gearSlotStack = slotStacks.get(ForgeMenu.GEAR_SLOT);
        if (gearSlotStack.hasTag())
            resultStack.setTag(gearSlotStack.getTag());
        resultStack.setDamageValue(0);
        ItemStack resultSlotStack = slotStacks.get(ForgeMenu.RESULT_SLOT);
        if (resultSlotStack.isEmpty()) {
            slotStacks.set(ForgeMenu.RESULT_SLOT, resultStack.copy());
        }
        else if (resultSlotStack.is(resultStack.getItem())) {
            resultSlotStack.grow(resultStack.getCount());
        }

        slotStacks.get(ForgeMenu.INGREDIENT_SLOT).shrink(((ForgeRecipe)recipe).getIngredientAmount());
        gearSlotStack.shrink(1);
        return true;
    }

    private static int getSmashesRequired(Level pLevel, ForgeBlockEntity pBlockEntity) {
        return pBlockEntity.quickCheck.getRecipeFor(pBlockEntity, pLevel).map(ForgeRecipe::getSmashesRequired).orElse(10);
    }

    //TODO Allow automatic forging via Dispenser
    @Override
    public int[] getSlotsForFace(Direction pSide) {
        if (pSide == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        }
        else {
            if (pSide != Direction.UP) {
                return SLOTS_FOR_SIDES;
            }

            return SLOTS_FOR_UP;
        }
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
        return Component.translatable(SurvivalReimagined.MOD_ID + ".container.forge");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new ForgeMenu(pContainerId, pInventory, this, this.dataAccess);
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
        boolean flag = !pStack.isEmpty() && ItemStack.isSameItemSameTags(pStack, itemstack);
        this.items.set(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        if (pSlot < ForgeMenu.RESULT_SLOT && !flag) {
            this.smashesRequired = getSmashesRequired(this.level, this);
            this.smashes = 0;
            this.setChanged();
        }

        if (this.level instanceof ServerLevel serverLevel)
            SyncForgeStatus.sync(serverLevel, this.getBlockPos(), this);
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

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec) {
        List<Recipe<?>> list = Lists.newArrayList();

        for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((p_155023_) -> {
                list.add(p_155023_);
                createExperience(pLevel, pPopVec, entry.getIntValue(), ((ForgeRecipe)p_155023_).getExperience());
            });
        }

        return list;
    }

    private void createExperience(ServerLevel pLevel, Vec3 pPopVec, int recipeAmount, float experience) {
        int xp = Mth.floor((float)recipeAmount * experience);
        float mod = Mth.frac((float)recipeAmount * experience);
        if (mod != 0.0F && Math.random() < (double)mod) {
            ++xp;
        }

        ExperienceOrb.award(pLevel, pPopVec, xp);
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer pPlayer) {
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience((ServerLevel) pPlayer.level(), pPlayer.position());
        pPlayer.awardRecipes(list);
        this.recipesUsed.clear();
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
