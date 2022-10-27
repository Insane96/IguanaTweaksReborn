package insane96mcp.iguanatweaksreborn.module.mining.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Misc", description = "Various mining changes")
public class MiningMisc extends Feature {
	private final ForgeConfigSpec.ConfigValue<Boolean> instaMineSilverfishConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> instaMineHeadsConfig;

	public boolean instaMineSilverfish = true;
	public boolean instaMineHeads = true;

	public MiningMisc(Module module) {
		super(ITCommonConfig.builder, module);
		this.pushConfig(ITCommonConfig.builder);
		this.instaMineSilverfishConfig = ITCommonConfig.builder
				.comment("Silverfish blocks will insta-mine like pre-1.17")
				.define("Insta-Mine Silverfish", this.instaMineSilverfish);
		this.instaMineHeadsConfig = ITCommonConfig.builder
				.comment("Heads will insta-break")
				.define("Insta-Mine Heads", this.instaMineHeads);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();

		this.instaMineSilverfish = this.instaMineSilverfishConfig.get();
		this.instaMineHeads = this.instaMineHeadsConfig.get();
	}

	@SubscribeEvent
	public void onBreak(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		silverfishBreakSpeed(event);
		skullBreakSpeed(event);
	}

	public void silverfishBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.instaMineSilverfish)
			return;

		if (event.getState().getBlock() instanceof InfestedBlock)
			event.setNewSpeed(Float.MAX_VALUE);
	}

	public void skullBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.instaMineHeads)
			return;

		if (event.getState().getBlock() instanceof AbstractSkullBlock)
			event.setNewSpeed(Float.MAX_VALUE);
	}
}
