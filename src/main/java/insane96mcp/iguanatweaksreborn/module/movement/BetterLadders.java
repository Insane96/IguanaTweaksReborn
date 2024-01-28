package insane96mcp.iguanatweaksreborn.module.movement;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Better Ladders", description = "Player's slides down ladders faster and stands still when opening an interface. This is disabled if quark is enabled")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class BetterLadders extends Feature {

	@Config(min = 0, max = 5d)
	@Label(name = "Speed", description = "How much faster the players moves down ladders")
	public static Double speed = 0.2d;

	public BetterLadders(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.phase == TickEvent.Phase.END
				|| !(event.player instanceof LocalPlayer localPlayer))
			return;

		boolean scaffold = localPlayer.level().getBlockState(localPlayer.blockPosition()).isScaffolding(localPlayer);
		if (localPlayer.isCrouching() == scaffold
				&& localPlayer.getRotationVector().x > 75f
				&& localPlayer.onClimbable()
				&& localPlayer.zza == 0f
				&& !localPlayer.input.jumping
				&& !localPlayer.onGround()
				&& !localPlayer.getAbilities().flying) {
			localPlayer.move(MoverType.SELF, new Vec3(0, -speed.floatValue(), 0));
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onInput(MovementInputUpdateEvent event) {
		if(!this.isEnabled())
			return;

		Player player = event.getEntity();
		if (player.onClimbable()
				&& Minecraft.getInstance().screen != null
				&& !player.getAbilities().flying
				&& !player.level().getBlockState(player.blockPosition()).isScaffolding(player)
				&& !(player.zza == 0 && player.getXRot() > 75f)
				&& !player.onGround()) {
			Input input = event.getInput();
			input.shiftKeyDown = true;
		}
	}

}