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
                            Codec.INT.optionalFieldOf("amount_to_replace", -1).forGetter(m -> m.amountToReplace),
                            Codec.list(Codec.FLOAT).optionalFieldOf("chances", List.of(1f)).forGetter(m -> m.chances),
                            Codec.list(Codec.FLOAT).optionalFieldOf("multipliers", List.of(1f)).forGetter(m -> m.multipliers),
                            Codec.BOOL.fieldOf("chests_only").forGetter(m -> m.chestsOnly)
                    )).apply(inst, ReplaceDropModifier::new)
            ));

    //Item to replace
    private final Item itemToReplace;
    //Item to replace with
    private final Item newItem;
    //List of multipliers, where the first is for no-fortune applied and the subsequent ones are for increasing level of fortune
    private int amountToReplace;
    //List of chances, where the first is for no-fortune applied and the subsequent ones are for increasing level of fortune
    private List<Float> chances;
    //List of multipliers, where the first is for no-fortune applied and the subsequent ones are for increasing level of fortune
    private List<Float> multipliers;
    private boolean chestsOnly;

    public ReplaceDropModifier(LootItemCondition[] conditionsIn, Item itemToReplace, Item newItem) {
        this(conditionsIn, itemToReplace, newItem, -1, List.of(1f), List.of(1f), false);
    }

    public ReplaceDropModifier(LootItemCondition[] conditionsIn, Item itemToReplace, Item newItem, int amountToReplace, List<Float> chances, List<Float> multipliers, boolean chestsOnly) {
        super(conditionsIn);
        this.itemToReplace = itemToReplace;
        this.newItem = newItem;
        this.amountToReplace = amountToReplace;
        this.chances = chances;
        this.multipliers = multipliers;
        this.chestsOnly = chestsOnly;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (this.chestsOnly && !context.getQueriedLootTableId().getPath().contains("chests/"))
            return generatedLoot;

        AtomicInteger amount = new AtomicInteger();
        generatedLoot.stream().filter(stack -> stack.getItem().equals(itemToReplace))
                .forEach(stack -> amount.addAndGet(stack.getCount()));
        if (amount.get() == 0)
            return generatedLoot;

        ItemStack itemstack = context.getParamOrNull(LootContextParams.TOOL);
        int i = itemstack != null ? itemstack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE) : 0;
        float chance = this.chances.get(Math.min(i, this.chances.size() - 1));
        if (context.getRandom().nextDouble() >= chance)
            return generatedLoot;

        float multiplier = this.multipliers.get(Math.min(i, this.multipliers.size() - 1));

        generatedLoot.removeIf(stack -> stack.getItem().equals(itemToReplace));
        if (amountToReplace == -1) {
            generatedLoot.add(new ItemStack(newItem, (int) (amount.get() * multiplier)));
        }
        else {
            ItemStack replacedStack = new ItemStack(this.newItem, (int) (Math.min(amount.get(), amountToReplace) * multiplier));
            generatedLoot.add(replacedStack);
            if (amountToReplace < amount.get()) {
                ItemStack oldStack = new ItemStack(this.itemToReplace, amount.get() - amountToReplace);
                generatedLoot.add(oldStack);
            }
        }
        return generatedLoot;
    }

    public static class Builder {
        final ReplaceDropModifier replaceDropModifier;
        public Builder(Item itemToReplace, Item newItem) {
            this(new LootItemCondition[0], itemToReplace, newItem);
        }

        public Builder(LootItemCondition[] conditionsIn, Item itemToReplace, Item newItem) {
            replaceDropModifier = new ReplaceDropModifier(conditionsIn, itemToReplace, newItem);
        }

        public Builder setAmountToReplace(int amount) {
            replaceDropModifier.amountToReplace = amount;
            return this;
        }

        public Builder setChances(List<Float> chances) {
            replaceDropModifier.chances = chances;
            return this;
        }

        public Builder setMultipliers(List<Float> multipliers) {
            replaceDropModifier.multipliers = multipliers;
            return this;
        }

        public Builder applyToChestsOnly() {
            replaceDropModifier.chestsOnly = true;
            return this;
        }

        public ReplaceDropModifier build() {
            return replaceDropModifier;
        }
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
