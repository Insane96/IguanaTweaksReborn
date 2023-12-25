package insane96mcp.iguanatweaksreborn.module.movement;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Elytra Nerf", description = "Makes flying with elytra in certain dimension have stronger gravity.")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class ElytraNerf extends Feature {

	@Config(min = 0d, max = 1d)
	@Label(name = "Strength", description = "How much the player is pulled down when flying with elytra in non-end dimensions.")
	public static Double pullStrength = 0.45d;

	public ElytraNerf(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.phase != TickEvent.Phase.START
				|| !event.player.isFallFlying()
				|| event.player.level().dimension() == Level.END)
			return;

		event.player.move(MoverType.SELF, new Vec3(0, -pullStrength, 0));
	}
}