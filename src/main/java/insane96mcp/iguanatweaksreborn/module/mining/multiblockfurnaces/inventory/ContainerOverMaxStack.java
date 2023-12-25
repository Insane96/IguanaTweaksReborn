package insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ContainerOverMaxStack extends SimpleContainer {
    public ContainerOverMaxStack(int pSize) {
        super(pSize);
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        this.items.set(pIndex, pStack);
        this.setChanged();
    }
}
