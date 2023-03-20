package insane96mcp.survivalreimagined.data.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class LootPurgerModifier extends LootModifier {
    public static final Supplier<Codec<LootPurgerModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(
                    inst.group(
                            ResourceLocation.CODEC.fieldOf("loot_table").forGetter(m -> m.lootTable),
                            Codec.INT.optionalFieldOf("start_range", 0).forGetter(m -> m.startRange),
                            Codec.INT.fieldOf("end_range").forGetter(m -> m.endRange),
                            Codec.FLOAT.optionalFieldOf("multiplier_at_start", 0f).forGetter(m -> m.multiplierAtStart),
                            Codec.BOOL.optionalFieldOf("apply_to_damageable", false).forGetter(m -> m.applyToDamageable)
                    )).apply(inst, LootPurgerModifier::new)
            ));

    private final ResourceLocation lootTable;
    private final int startRange;
    private final int endRange;
    //Chance to purge when at start range
    private final float multiplierAtStart;
    private final boolean applyToDamageable;

    public LootPurgerModifier(LootItemCondition[] conditionsIn, ResourceLocation lootTable, int endRange) {
        this(conditionsIn, lootTable, 0, endRange, 0f, true);
    }

    public LootPurgerModifier(LootItemCondition[] conditionsIn, ResourceLocation lootTable, int startRange, int endRange, float multiplierAtStart, boolean applyToDamageable) {
        super(conditionsIn);
        this.lootTable = lootTable;
        this.startRange = startRange;
        this.endRange = endRange;
        this.multiplierAtStart = multiplierAtStart;
        this.applyToDamageable = applyToDamageable;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.getQueriedLootTableId().equals(this.lootTable)
                || context.getParamOrNull(LootContextParams.ORIGIN) == null)
            return generatedLoot;

        int spawnX = context.getLevel().getLevelData().getXSpawn();
        int spawnZ = context.getLevel().getLevelData().getZSpawn();
        int x = (int) context.getParam(LootContextParams.ORIGIN).x;
        int z = (int) context.getParam(LootContextParams.ORIGIN).z;
        int distanceFromSpawn = (int) Math.sqrt((x - spawnX) * (x - spawnX) + (z - spawnZ) * (z - spawnZ));
        int distanceFromStart = (distanceFromSpawn - this.startRange);
        float multiplier = (this.endRange - distanceFromStart) / ((float) this.endRange - this.startRange) * (1f - multiplierAtStart);
        generatedLoot.removeIf(itemStack -> context.getRandom().nextDouble() < multiplier);
        generatedLoot.forEach(itemStack -> {
            if (itemStack.getItem().canBeDepleted())
                itemStack.setDamageValue((int) ((itemStack.getMaxDamage() - itemStack.getDamageValue()) * (1f - multiplier)));
        });
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}