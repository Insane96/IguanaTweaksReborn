package insane96mcp.iguanatweaksreborn.data.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.iguanatweaksreborn.setup.ITGlobalLootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class RawIronDropModifier extends LootModifier {
    public static final RegistryObject<Codec<RawIronDropModifier>> CODEC = ITGlobalLootModifiers.LOOT_MODIFIERS.register("raw_iron_drop_modifier", () ->
            RecordCodecBuilder.create(inst ->
                    codecStart(inst).apply(inst, RawIronDropModifier::new)
            ));

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public RawIronDropModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        AtomicInteger rawIronAmount = new AtomicInteger();
        generatedLoot.stream().filter(stack -> stack.getItem() == Items.RAW_IRON)
                .forEach(stack -> rawIronAmount.addAndGet(stack.getCount()));
        if (rawIronAmount.get() == 0)
            return generatedLoot;

        generatedLoot.removeIf(stack -> stack.getItem() == Items.RAW_IRON);
        generatedLoot.add(new ItemStack(Items.IRON_NUGGET, rawIronAmount.get()));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
