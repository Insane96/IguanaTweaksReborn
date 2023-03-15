package insane96mcp.survivalreimagined.data.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.insanelib.util.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class DropMultiplierModifier extends LootModifier {
    public static final Supplier<Codec<DropMultiplierModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(
                    inst.group(
                            ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item),
                            Codec.floatRange(0f, 1024f).fieldOf("multiplier").forGetter(m -> m.multiplier),
                            Codec.intRange(0, 256).fieldOf("amount_to_keep").forGetter(m -> m.amountToKeep)
                    )).apply(inst, DropMultiplierModifier::new)
            ));

    //The item to modify
    private final Item item;
    //The multiplier applied to the amount of items
    private final float multiplier;
    //This amount is subtracted from the total amount before applying the multiplier
    private final int amountToKeep;

    public DropMultiplierModifier(LootItemCondition[] conditionsIn, Item item, float multiplier) {
        this(conditionsIn, item, multiplier, 0);
    }

    public DropMultiplierModifier(LootItemCondition[] conditionsIn, Item item, float multiplier, int amountToKeep) {
        super(conditionsIn);
        this.item = item;
        this.multiplier = multiplier;
        this.amountToKeep = amountToKeep;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack newStack = null;
        List<ItemStack> stream = generatedLoot.stream().filter(stack -> stack.getItem().equals(item)).toList();
        for (ItemStack stack : stream) {
            if (newStack == null)
                newStack = stack.copy();
            else
                newStack.setCount(newStack.getCount() + stack.getCount());
        }
        if (newStack == null)
            return generatedLoot;
        generatedLoot.removeIf(stack -> stack.getItem().equals(item));
        //Remove the amount to keep and multiply the remainder with the multiplier
        //If the result has a decimal part, treat that decimal part as a chance to have +1 count
        newStack.setCount(MathHelper.getAmountWithDecimalChance(context.getRandom(), (newStack.getCount() - amountToKeep) * multiplier) + amountToKeep);
        generatedLoot.add(newStack);

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
