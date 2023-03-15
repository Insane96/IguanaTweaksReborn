package insane96mcp.survivalreimagined.module.client.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.ClientModules;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Fog", description = "Makes fog less invasive in some contexts")
@LoadFeature(module = ClientModules.Ids.CLIENT)
public class Fog extends Feature {

    @Config
    @Label(name = "Better visibility in Lava with Fire Resistance Lava", description = "If true you'll be able to see better in lava when with the Fire Resistance Effect.")
    public static Boolean betterFireResistanceLavaFog = true;
    @Config
    @Label(name = "Better Nether Fog", description = "If true Nether Fog is no longer limited to 12 chunks.")
    public static Boolean betterNetherFog = true;
    @Config(min = 0d, max = 1d)
    @Label(name = "Nether Fog Ratio", description = "Render distance is multiplied by this value in the Nether. Vanilla is 0.5.")
    public static Double netherFogRatio = 0.75d;

    public Fog(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onFog(ViewportEvent.RenderFog event) {
        if (!this.isEnabled())
            return;

        lavaFog(event);
        netherFog(event);
    }

    public void lavaFog(ViewportEvent.RenderFog event) {
        if (!betterFireResistanceLavaFog
                || event.getCamera().getFluidInCamera() != FogType.LAVA
                || event.getCamera().getEntity().isSpectator())
            return;

        Entity entity = event.getCamera().getEntity();
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            event.setNearPlaneDistance(-16);
            event.setFarPlaneDistance(16f);
        }

        event.setCanceled(true);
    }

    public void netherFog(ViewportEvent.RenderFog event) {
        if (!betterNetherFog
                || event.isCanceled())
            return;

        Entity entity = event.getCamera().getEntity();
        if (entity.level.dimension() == Level.NETHER) {
            float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
            event.setNearPlaneDistance((float) (renderDistance * netherFogRatio / 10f));
            event.setFarPlaneDistance((float) (renderDistance * netherFogRatio));
            event.setCanceled(true);
        }
    }
}
