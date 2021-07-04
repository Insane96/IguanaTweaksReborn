package insane96mcp.iguanatweaksreborn.modules.mining.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

@Label(name = "Wrong Tool", description = "Disables the mining of blocks in case you don't have the right tool")
public class WrongToolFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> disableMiningWrongToolConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> requireEfficientToolConfig;

	public Boolean disableMiningWrongTool = true;
	public Boolean requireEfficientTool = true;

	public WrongToolFeature(Module module) {
		super(Config.builder, module, false);
		Config.builder.comment(this.getDescription()).push(this.getName());
		disableMiningWrongToolConfig = Config.builder
				.comment("While true, blocks that require a tool (e.g. stone) will not be mineable without the tool required.\n" +
						"Insta-break blocks are ignored.")
				.define("Disable Mining with Wrong Tool", this.disableMiningWrongTool);
		requireEfficientToolConfig = Config.builder
				.comment("While true, blocks that have a proper tool (e.g. wood) will not be minable without a proper tool.\n" +
						"Insta-break blocks are ignored.")
				.define("Require Efficient Tool", this.requireEfficientTool);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.disableMiningWrongTool = this.disableMiningWrongToolConfig.get();
		this.requireEfficientTool = this.requireEfficientToolConfig.get();
	}

	@SubscribeEvent
	public void disableMining(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;
		World world = event.getPlayer().world;
		BlockState blockState = world.getBlockState(event.getPos());
		if (blockState.hardness == 0f)
			return;
		if (requiredTool(blockState) || efficientTool(blockState, event.getPlayer().getHeldItemMainhand()))
			event.setNewSpeed(-1);
	}

	private boolean requiredTool(BlockState blockState) {
		if (!this.disableMiningWrongTool)
			return false;
		return blockState.getRequiresTool();
	}

	private boolean efficientTool(BlockState blockState, ItemStack itemStack) {
		if (!this.requireEfficientTool)
			return false;
		Set<ToolType> toolTypes = itemStack.getToolTypes();
		for (ToolType toolType : toolTypes) {
			if (blockState.getHarvestTool().equals(toolType))
				return false;
		}
		return true;
	}
}
