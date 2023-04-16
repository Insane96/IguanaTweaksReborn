package insane96mcp.survivalreimagined.module.items.item;

import insane96mcp.survivalreimagined.module.items.block.CrateBlock;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.stream.Stream;

public class CrateItem extends BlockItem {

    public CrateItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return super.canFitInsideContainerItems() && !(this.getBlock() instanceof CrateBlock);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150775_) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        getContents(p_150775_).forEach(nonnulllist::add);
        return Optional.of(new BundleTooltip(nonnulllist, 1));
    }

    private static Stream<ItemStack> getContents(ItemStack stack) {
        CompoundTag compoundtag = BlockItem.getBlockEntityData(stack);
        if (compoundtag != null) {
            if (compoundtag.contains("LootTable", 8)) {
                return Stream.empty();
            }

            ListTag listtag = compoundtag.getList("Items", 10);
            return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
        }
        return Stream.empty();
    }
}
