package insane96mcp.survivalreimagined.data.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class InjectLootTableModifier extends LootModifier {
    public static final Supplier<Codec<InjectLootTableModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(
                    ResourceLocation.CODEC.fieldOf("loot_table").forGetter(m -> m.lootTable)
                    ).apply(inst, InjectLootTableModifier::new)
            ));

    private final ResourceLocation lootTable;

    public InjectLootTableModifier(LootItemCondition[] conditionsIn, ResourceLocation lootTable) {
        super(conditionsIn);
        this.lootTable = lootTable;
    }

    public InjectLootTableModifier(ResourceLocation lootTableToInjectTo, ResourceLocation lootTable) {
        super(new LootItemCondition[] {new LootTableIdCondition.Builder(lootTableToInjectTo).build()});
        this.lootTable = lootTable;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        LootTable lootTable1 = context.getResolver().getLootTable(this.lootTable);
        lootTable1.getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
