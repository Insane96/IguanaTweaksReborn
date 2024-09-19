package insane96mcp.iguanatweaksreborn.module.items;

import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import insane96mcp.insanelib.util.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Ecologic Wood", description = "Wooden items have a lower chance to break in sunlight.")
@LoadFeature(module = Modules.Ids.ITEMS)
public class EcologicWood extends Feature {
    public static final TagKey<Item> WOODEN_HAND_EQUIPMENT = ITRItemTagsProvider.create("equipment/hand/wooden");

    public EcologicWood(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void processItemDamaging(HurtItemStackEvent event) {
        if (!this.isEnabled()
                || event.getPlayer() == null
                || !event.getStack().is(WOODEN_HAND_EQUIPMENT))
            return;

        float skyLightRatio = getCalculatedSkyLightRatio(event.getPlayer());
        float ratio = 1f - (0.75f * skyLightRatio);
        float amount = event.getAmount() * ratio;
        event.setAmount(MathHelper.getAmountWithDecimalChance(event.getRandom(), amount));
    }

    public static float getCalculatedSkyLight(Entity entity) {
        return getCalculatedSkyLight(entity.level(), entity.blockPosition());
    }

    public static float getCalculatedSkyLight(Level level, BlockPos pos) {
        if (!level.isDay()
                || level.isThundering())
            return 0f;
        float skyLight = level.getBrightness(LightLayer.SKY, pos) - level.getSkyDarken();
        if (level.isRaining())
            skyLight /= 3f;
        return skyLight;
    }

    /**
     * Returns a value between 0 and 1 where 0 is total darkness and 1 is 15 light level
     */
    public static float getCalculatedSkyLightRatio(Entity entity) {
        return getCalculatedSkyLightRatio(entity.level(), entity.blockPosition());
    }

    public static float getCalculatedSkyLightRatio(Level level, BlockPos pos) {
        return Math.min(getCalculatedSkyLight(level, pos), 12f) / 12f;
    }
}