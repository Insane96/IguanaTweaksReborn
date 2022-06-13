package insane96mcp.iguanatweaksreborn.module.client.feature;

import insane96mcp.iguanatweaksreborn.setup.ITClientConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Fog", description = "Makes fog less invasive in some contexts")
public class Fog extends Feature {

    private final ForgeConfigSpec.BooleanValue betterFireResistanceLavaFogConfig;
    private final ForgeConfigSpec.BooleanValue betterNetherFogConfig;
    private final ForgeConfigSpec.DoubleValue netherFogRatioConfig;

    public boolean betterFireResistanceLavaFog = true;
    public boolean betterNetherFog = true;
    public double netherFogRatio = 0.75d;

    public Fog(Module module) {
        super(ITClientConfig.builder, module);
        this.pushConfig(ITClientConfig.builder);
        betterFireResistanceLavaFogConfig = ITClientConfig.builder
                .comment("If true you'll be able to see better in lava when with the Fire Resistance Effect.")
                .define("Better visibility in Lava with Fire Resistance Lava", betterFireResistanceLavaFog);
        betterNetherFogConfig = ITClientConfig.builder
                .comment("If true Nether Fog is no longer limited to 12 chunks.")
                .define("Better Nether Fog", betterNetherFog);
        netherFogRatioConfig = ITClientConfig.builder
                .comment("Render distance is multiplied by this value in the Nether. Vanilla is 0.5.")
                .defineInRange("Nether Fog Ratio", netherFogRatio, 0d, 1d);
        ITClientConfig.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.betterFireResistanceLavaFog = this.betterFireResistanceLavaFogConfig.get();
        this.betterNetherFog = this.betterNetherFogConfig.get();
        this.netherFogRatio = this.netherFogRatioConfig.get();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (!this.isEnabled())
            return;

        lavaFog(event);
        netherFog(event);
    }

    public void lavaFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (!this.betterFireResistanceLavaFog
                || event.getCamera().getFluidInCamera() != FogType.LAVA
                || event.getCamera().getEntity().isSpectator())
            return;

        Entity entity = event.getCamera().getEntity();
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            event.setNearPlaneDistance(-8);
            event.setFarPlaneDistance(24f);
        }

        event.setCanceled(true);
    }

    public void netherFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (!this.betterNetherFog
                || event.isCanceled())
            return;

        Entity entity = event.getCamera().getEntity();
        if (entity.level.dimension().location().equals(DimensionType.NETHER_LOCATION.location())) {
            float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
            event.setNearPlaneDistance((float) (renderDistance * this.netherFogRatio / 10f));
            event.setFarPlaneDistance((float) (renderDistance * this.netherFogRatio));
        }
        event.setCanceled(true);
    }
}
