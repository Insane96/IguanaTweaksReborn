package insane96mcp.survivalreimagined.data.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.insanelib.util.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DropMultiplierModifier extends LootModifier {
    public static final Supplier<Codec<DropMultiplierModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(
                    inst.group(
                            ForgeRegistries.ITEMS.getCodec().optionalFieldOf("item").forGetter(m -> m.item),
                            TagKey.codec(Registries.ITEM).optionalFieldOf("tag").forGetter(m -> m.tag),
                            Codec.floatRange(0f, 1024f).fieldOf("multiplier").forGetter(m -> m.multiplier),
                            Codec.intRange(0, 256).fieldOf("amount_to_keep").forGetter(m -> m.amountToKeep)
                    )).apply(inst, DropMultiplierModifier::new)
            ));

    //The item to modify
    private final Optional<Item> item;
    //The item tag to modify
    private final Optional<TagKey<Item>> tag;
    //The multiplier applied to the amount of items
    private final float multiplier;
    //This amount is subtracted from the total amount before applying the multiplier
    private final int amountToKeep;

    public DropMultiplierModifier(LootItemCondition[] conditionsIn, Item item, float multiplier, int amountToKeep) {
        this(conditionsIn, Optional.of(item), Optional.empty(), multiplier, amountToKeep);
    }

    public DropMultiplierModifier(LootItemCondition[] conditionsIn, TagKey<Item> tag, float multiplier, int amountToKeep) {
        this(conditionsIn, Optional.empty(), Optional.of(tag), multiplier, amountToKeep);
    }


    public DropMultiplierModifier(LootItemCondition[] conditionsIn, Item item, float multiplier) {
        this(conditionsIn, Optional.of(item), Optional.empty(), multiplier, 0);
    }

    public DropMultiplierModifier(LootItemCondition[] conditionsIn, TagKey<Item> tag, float multiplier) {
        this(conditionsIn, Optional.empty(), Optional.of(tag), multiplier, 0);
    }

    public DropMultiplierModifier(LootItemCondition[] conditionsIn, Optional<Item> item, Optional<TagKey<Item>> tag, float multiplier, int amountToKeep) {
        super(conditionsIn);
        this.item = item;
        this.tag = tag;
        this.multiplier = multiplier;
        this.amountToKeep = amountToKeep;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Predicate<ItemStack> MATCHES_ITEM_OR_TAG = stack -> {
            if (item.isPresent()) {
                return stack.is(item.get());
            }
            else if (tag.isPresent()) {
                return stack.is(tag.get());
            }
            return false;
        };
        List<ItemStack> stream = generatedLoot.stream().filter(MATCHES_ITEM_OR_TAG).toList();
        if (stream.isEmpty())
            return generatedLoot;
        ItemStack newStack = null;

        for (ItemStack stack : stream) {
            if (newStack == null)
                newStack = stack.copy();
            else
                newStack.setCount(newStack.getCount() + stack.getCount());
        }
        generatedLoot.removeIf(MATCHES_ITEM_OR_TAG);
        //Remove the amount to keep and multiply the remainder with the multiplier
        //If the result has a decimal part, treat that decimal part as a chance to have +1 count
        int count = MathHelper.getAmountWithDecimalChance(context.getRandom(), (newStack.getCount() - amountToKeep) * multiplier) + amountToKeep;
        if (count > 0) {
            newStack.setCount(count);
            generatedLoot.add(newStack);
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
