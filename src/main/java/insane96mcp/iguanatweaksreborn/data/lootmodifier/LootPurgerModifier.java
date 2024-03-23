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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class LootPurgerModifier extends LootModifier {
    public static final Supplier<Codec<LootPurgerModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(
                    inst.group(
                            Codec.INT.optionalFieldOf("start_range", 0).forGetter(m -> m.startRange),
                            Codec.INT.fieldOf("end_range").forGetter(m -> m.endRange),
                            Codec.FLOAT.optionalFieldOf("multiplier_at_start", 0f).forGetter(m -> m.multiplierAtStart),
                            Codec.BOOL.optionalFieldOf("apply_to_damageable", false).forGetter(m -> m.applyToDamageable),
                            TagKey.codec(Registries.ITEM).optionalFieldOf("blacklisted_items_tag").forGetter(m -> m.blacklistedItemsTag)
                    )).apply(inst, LootPurgerModifier::new)
            ));

    private int startRange = 0;
    private int endRange;
    //Chance to purge when at start range
    private float multiplierAtStart = 0f;
    private boolean applyToDamageable = false;
    private Optional<TagKey<Item>> blacklistedItemsTag;

    public LootPurgerModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public LootPurgerModifier(LootItemCondition[] conditionsIn, int startRange, int endRange, float multiplierAtStart, boolean applyToDamageable, Optional<TagKey<Item>> blacklistedItemsTag) {
        super(conditionsIn);
        this.startRange = startRange;
        this.endRange = endRange;
        this.multiplierAtStart = multiplierAtStart;
        this.applyToDamageable = applyToDamageable;
        this.blacklistedItemsTag = blacklistedItemsTag;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getParamOrNull(LootContextParams.ORIGIN) == null)
            return generatedLoot;

        int spawnX = context.getLevel().getLevelData().getXSpawn();
        int spawnZ = context.getLevel().getLevelData().getZSpawn();
        int x = (int) context.getParam(LootContextParams.ORIGIN).x;
        int z = (int) context.getParam(LootContextParams.ORIGIN).z;
        int distanceFromSpawn = (int) Math.sqrt((x - spawnX) * (x - spawnX) + (z - spawnZ) * (z - spawnZ));
        int distanceFromStart = (distanceFromSpawn - this.startRange);
        //Chance to purge
        float multiplier;
        if (distanceFromStart <= 0)
            multiplier = 1f - this.multiplierAtStart;
        else
            multiplier = (this.endRange - distanceFromStart) / ((float) this.endRange - this.startRange) * (1f - this.multiplierAtStart);
        generatedLoot.removeIf(itemStack -> {
            if (blacklistedItemsTag.isPresent() && itemStack.is(blacklistedItemsTag.get()))
                return false;
            return context.getRandom().nextDouble() < multiplier;
        });
        if (this.applyToDamageable) {
            generatedLoot.forEach(itemStack -> {
                if (itemStack.getItem().canBeDepleted())
                    itemStack.setDamageValue((int) (itemStack.getMaxDamage() - ((itemStack.getMaxDamage() - itemStack.getDamageValue()) * (1f - multiplier))));
            });
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public static class Builder {
        final LootPurgerModifier lootPurgerModifier;

        public Builder(LootItemCondition[] conditionsIn, int endRange) {
            this.lootPurgerModifier = new LootPurgerModifier(conditionsIn);
            this.lootPurgerModifier.endRange = endRange;
        }

        public Builder(ResourceLocation lootTable, int endRange) {
            this(new LootItemCondition[] { LootTableIdCondition.builder(lootTable).build() }, endRange);
        }

        public Builder setStartRange(int startRange) {
            this.lootPurgerModifier.startRange = startRange;
            return this;
        }

        public Builder setMultiplierAtStart(float multiplierAtStart) {
            this.lootPurgerModifier.multiplierAtStart = multiplierAtStart;
            return this;
        }

        public Builder applyToDamageable() {
            this.lootPurgerModifier.applyToDamageable = true;
            return this;
        }

        public Builder blacklistedItemTag(TagKey<Item> tag) {
            this.lootPurgerModifier.blacklistedItemsTag = Optional.of(tag);
            return this;
        }

        public LootPurgerModifier build() {
            return this.lootPurgerModifier;
        }
    }
}