package insane96mcp.iguanatweaksreborn.data.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ReplaceDropModifier extends LootModifier {
    public static final Supplier<Codec<ReplaceDropModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(
                    inst.group(
                            ForgeRegistries.ITEMS.getCodec().fieldOf("item_to_replace").forGetter(m -> m.itemToReplace),
                            ForgeRegistries.ITEMS.getCodec().fieldOf("new_item").forGetter(m -> m.newItem)
                    )).apply(inst, ReplaceDropModifier::new)
            ));

    private final Item itemToReplace;
    private final Item newItem;

    public ReplaceDropModifier(LootItemCondition[] conditionsIn, Item itemToReplace, Item newItem) {
        super(conditionsIn);
        this.itemToReplace = itemToReplace;
        this.newItem = newItem;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        AtomicInteger amount = new AtomicInteger();
        generatedLoot.stream().filter(stack -> stack.getItem().equals(itemToReplace))
                .forEach(stack -> amount.addAndGet(stack.getCount()));
        if (amount.get() == 0)
            return generatedLoot;

        generatedLoot.removeIf(stack -> stack.getItem().equals(itemToReplace));
        generatedLoot.add(new ItemStack(newItem, amount.get()));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
