package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.combat.PiercingPickaxes;
import insane96mcp.survivalreimagined.module.items.copper.CopperToolsExpansion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SRDamageTypeTagsProvider extends DamageTypeTagsProvider {

    public SRDamageTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).addTag(PiercingPickaxes.PIERCING_DAMAGE_TYPE);

        tag(PiercingPickaxes.PIERCING_DAMAGE_TYPE).add(PiercingPickaxes.PIERCING_MOB_ATTACK, PiercingPickaxes.PIERCING_PLAYER_ATTACK);
        tag(PiercingPickaxes.DOESNT_TRIGGER_PIERCING).addTag(PiercingPickaxes.PIERCING_DAMAGE_TYPE).add(CopperToolsExpansion.ELECTROCUTION_ATTACK);

        tag(CopperToolsExpansion.DOESNT_TRIGGER_ELECTROCUTION).addTag(PiercingPickaxes.PIERCING_DAMAGE_TYPE).add(CopperToolsExpansion.ELECTROCUTION_ATTACK);
    }

    public static TagKey<DamageType> create(String tagName) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(SurvivalReimagined.MOD_ID, tagName));
    }
}
