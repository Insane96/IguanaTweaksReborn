package insane96mcp.iguanatweaksreborn.module.client.feature;

import insane96mcp.iguanatweaksreborn.setup.ITClientConfig;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
@Label(name = "Fog", description = "Makes fog less invasive in some contexts")
public class Fog extends Feature {
    public Fog(Module module) {
        super(ITClientConfig.builder, module);
    }

    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (!this.isEnabled())
            return;

        if (event.getCamera().getFluidInCamera() != FogType.LAVA)
            return;

        if (event.getCamera().getEntity().isSpectator())
            return;

        event.setNearPlaneDistance(10f);
    }
}
