package insane96mcp.iguanatweaksreborn.data.generator;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.BaneOfSSSS;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.Smite;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ITREntityTypeTagsProvider extends EntityTypeTagsProvider {

    public ITREntityTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(BaneOfSSSS.AFFECTED_BY_BANE_OF_SSSSS)
                .add(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SILVERFISH, EntityType.CREEPER);
        tag(Smite.AFFECTED_BY_SMITE)
                .add(EntityType.ZOMBIE, EntityType.HUSK, EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.SKELETON_HORSE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.PHANTOM);
    }
}
