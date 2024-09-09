package insane96mcp.iguanatweaksreborn.data.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class DisenchantModifier extends LootModifier {
    public static final Supplier<Codec<DisenchantModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(
                            TagKey.codec(Registries.ITEM).optionalFieldOf("blacklisted_items_tag").forGetter(m -> m.blacklistedItemsTag)
                    ).apply(inst, DisenchantModifier::new)
            ));

    private Optional<TagKey<Item>> blacklistedItemsTag;

    public DisenchantModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public DisenchantModifier(LootItemCondition[] conditionsIn, Optional<TagKey<Item>> blacklistedItemsTag) {
        super(conditionsIn);
        this.blacklistedItemsTag = blacklistedItemsTag;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        List<ItemStack> toRemove = new ArrayList<>();
        generatedLoot.forEach(itemStack -> {
            if (blacklistedItemsTag.isPresent() && itemStack.is(blacklistedItemsTag.get()))
                return;

            if (!itemStack.is(Items.ENCHANTED_BOOK))
                itemStack.removeTagKey("Enchantments");
            else {
                toRemove.add(itemStack);
            }
        });
        toRemove.forEach(stack -> {
            generatedLoot.remove(stack);
            generatedLoot.add(new ItemStack(Items.BOOK));
        });
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public static class Builder {
        final DisenchantModifier disenchantModifier;

        public Builder(LootItemCondition[] conditionsIn) {
            this.disenchantModifier = new DisenchantModifier(conditionsIn);
        }

        public Builder(ResourceLocation lootTable) {
            this(new LootItemCondition[] { LootTableIdCondition.builder(lootTable).build() });
        }

        public DisenchantModifier build() {
            return this.disenchantModifier;
        }
    }
}