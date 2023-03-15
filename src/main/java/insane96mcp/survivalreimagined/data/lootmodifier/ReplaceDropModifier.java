package insane96mcp.survivalreimagined.data.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ReplaceDropModifier extends LootModifier {
    public static final Supplier<Codec<ReplaceDropModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(
                    inst.group(
                            ForgeRegistries.ITEMS.getCodec().fieldOf("item_to_replace").forGetter(m -> m.itemToReplace),
                            ForgeRegistries.ITEMS.getCodec().fieldOf("new_item").forGetter(m -> m.newItem),
                            Codec.list(Codec.FLOAT).fieldOf("multipliers").forGetter(m -> m.multipliers)
                    )).apply(inst, ReplaceDropModifier::new)
            ));

    //Item to replace
    private final Item itemToReplace;
    //Item to replace with
    private final Item newItem;
    //List of multipliers, where the first is for no-fortune applied and the subsequent ones are for increasing level of fortune
    private final List<Float> multipliers;

    public ReplaceDropModifier(LootItemCondition[] conditionsIn, Item itemToReplace, Item newItem) {
        this(conditionsIn, itemToReplace, newItem, List.of(1f));
    }

    public ReplaceDropModifier(LootItemCondition[] conditionsIn, Item itemToReplace, Item newItem, List<Float> multipliers) {
        super(conditionsIn);
        this.itemToReplace = itemToReplace;
        this.newItem = newItem;
        this.multipliers = multipliers;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        AtomicInteger amount = new AtomicInteger();
        generatedLoot.stream().filter(stack -> stack.getItem().equals(itemToReplace))
                .forEach(stack -> amount.addAndGet(stack.getCount()));
        if (amount.get() == 0)
            return generatedLoot;

        generatedLoot.removeIf(stack -> stack.getItem().equals(itemToReplace));
        ItemStack itemstack = context.getParamOrNull(LootContextParams.TOOL);
        int i = itemstack != null ? itemstack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE) : 0;
        float multiplier = this.multipliers.get(Math.min(i, this.multipliers.size() - 1));
        generatedLoot.add(new ItemStack(newItem, (int) (amount.get() * multiplier)));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
