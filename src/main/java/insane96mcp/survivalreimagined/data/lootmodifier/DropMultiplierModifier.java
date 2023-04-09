package insane96mcp.survivalreimagined.data.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.insanelib.util.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
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
                            Codec.intRange(0, 256).optionalFieldOf("amount_to_keep", 0).forGetter(m -> m.amountToKeep)
                    )).apply(inst, DropMultiplierModifier::new)
            ));

    //The item to modify
    private Optional<Item> item;
    //The item tag to modify
    private Optional<TagKey<Item>> tag;
    //The multiplier applied to the amount of items
    private float multiplier;
    //This amount is subtracted from the total amount before applying the multiplier
    private int amountToKeep = 0;

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

    public static DropMultiplierModifier newItem(LootItemCondition[] conditionsIn, Optional<Item> item, float multiplier) {
        return new DropMultiplierModifier(conditionsIn, item, Optional.empty(), multiplier, 0);
    }

    public static DropMultiplierModifier newTag(LootItemCondition[] conditionsIn, Optional<TagKey<Item>> tag, float multiplier) {
        return new DropMultiplierModifier(conditionsIn, Optional.empty(), tag, multiplier, 0);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public static class Builder {
        final DropMultiplierModifier dropMultiplierModifier;

        public Builder(LootItemCondition[] conditionsIn, Item item, float multiplier) {
            this.dropMultiplierModifier = DropMultiplierModifier.newItem(conditionsIn, Optional.of(item), multiplier);
        }

        public Builder(LootItemCondition[] conditionsIn, TagKey<Item> tag, float multiplier) {
            this.dropMultiplierModifier = DropMultiplierModifier.newTag(conditionsIn, Optional.of(tag), multiplier);
        }

        public Builder(Item item, float multiplier) {
            this.dropMultiplierModifier = DropMultiplierModifier.newItem(new LootItemCondition[0], Optional.of(item), multiplier);
        }

        public Builder(TagKey<Item> tag, float multiplier) {
            this.dropMultiplierModifier = DropMultiplierModifier.newTag(new LootItemCondition[0], Optional.of(tag), multiplier);
        }

        public Builder(EntityType<?> entityType, Item item, float multiplier) {
            this.dropMultiplierModifier = DropMultiplierModifier.newItem(new LootItemCondition[] {LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(entityType)).build()).build()}, Optional.of(item), multiplier);
        }

        public Builder(EntityType<?> entityType, TagKey<Item> tag, float multiplier) {
            this.dropMultiplierModifier = DropMultiplierModifier.newTag(new LootItemCondition[] {LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(entityType)).build()).build()}, Optional.of(tag), multiplier);
        }

        public Builder keepAmount(int amount) {
            this.dropMultiplierModifier.amountToKeep = amount;
            return this;
        }

        public DropMultiplierModifier build() {
            return this.dropMultiplierModifier;
        }
    }
}
