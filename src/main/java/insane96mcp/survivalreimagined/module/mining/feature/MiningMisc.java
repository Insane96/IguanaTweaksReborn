package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Label(name = "Misc", description = "Various mining changes")
@LoadFeature(module = Modules.Ids.MINING)
public class MiningMisc extends Feature {

	public static final UUID BLOCK_REACH_REDUCTION_UUID = UUID.fromString("bae34f6a-c58e-4622-b2ab-f1b89b73b781");

	@Config
	@Label(name = "Insta-Mine Silverfish", description = "Silverfish blocks will insta-mine like pre-1.17")
	public static Boolean instaMineSilverfish = true;
	@Config
	@Label(name = "Insta-Mine Heads", description = "Heads will insta-break")
	public static Boolean instaMineHeads = true;

	@Config(min = -4, max = 0)
	@Label(name = "Mining Range reduction", description = "Reduce the range at which players can interact with blocks")
	public static Double miningRangeReduction = -1d;

	@Config
	@Label(name = "Fast slabs", description = "Makes slabs (not double slabs) take half the time to break")
	public static Boolean fastSlabs = true;

	public MiningMisc(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			if ((instaMineHeads && block instanceof AbstractSkullBlock)
					|| (instaMineSilverfish && block instanceof InfestedBlock))
				block.getStateDefinition().getPossibleStates().forEach(blockState -> blockState.destroySpeed = 0f);
		}
	}

	@SubscribeEvent
	public void onBreak(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| !fastSlabs
				|| !(event.getState().getBlock() instanceof SlabBlock))
			return;

		if (event.getState().getValue(SlabBlock.TYPE) == SlabType.TOP || event.getState().getValue(SlabBlock.TYPE) == SlabType.BOTTOM)
			event.setNewSpeed(event.getOriginalSpeed() * 2f);
	}

	/*public void silverfishBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!instaMineSilverfish)
			return;

		if (event.getState().getBlock() instanceof InfestedBlock)
			event.setNewSpeed(Float.MAX_VALUE);
	}*/

	/*public void skullBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!instaMineHeads)
			return;

		if (event.getState().getBlock() instanceof AbstractSkullBlock)
			event.setNewSpeed(Float.MAX_VALUE);
	}*/

	@SubscribeEvent
	public void onPlayerJoinLevel(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| miningRangeReduction == 0d
				|| !(event.getEntity() instanceof Player player))
			return;

		MCUtils.applyModifier(player, ForgeMod.BLOCK_REACH.get(), BLOCK_REACH_REDUCTION_UUID, "Block reach reduction", miningRangeReduction, AttributeModifier.Operation.ADDITION, false);
	}
}
