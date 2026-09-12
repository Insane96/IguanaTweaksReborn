package insane96mcp.insanesurvivaloverhaul.jade;

import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanesurvivaloverhaul.InsaneSO;
import insane96mcp.insanesurvivaloverhaul.module.combat.regeneratingabsorption.RegeneratingAbsorption;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

public enum ISORegeneratingAbsorptionComponentProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, Float> {
    INSTANCE;

    public static final ResourceLocation ID = InsaneSO.id("regenerating_absorption");

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!config.get(ID))
            return;
        float absorption = decodeFromData(accessor).orElse(0f);
        if (absorption <= 0f)
            return;
        tooltip.add(new RegeneratingAbsorptionElement(absorption).tag(ID));
    }

    @Override
    public boolean shouldRequestData(EntityAccessor accessor) {
        return Feature.isEnabled(RegeneratingAbsorption.class)
                && accessor.getEntity() instanceof LivingEntity living
                && RegeneratingAbsorption.getCappedMaxAbsorption(living) > 0;
    }

    @Override
    public @Nullable Float streamData(EntityAccessor accessor) {
        float absorption = RegeneratingAbsorption.getCurrentAbsorption((LivingEntity) accessor.getEntity());
        return absorption > 0f ? absorption : null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Float> streamCodec() {
        return ByteBufCodecs.FLOAT.cast();
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
