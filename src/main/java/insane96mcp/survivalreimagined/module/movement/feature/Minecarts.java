package insane96mcp.survivalreimagined.module.movement.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.movement.block.NetherInfusedRailBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

@Label(name = "Minecarts")
@LoadFeature(module = Modules.Ids.MOVEMENT, canBeDisabled = false)
public class Minecarts extends Feature {

	public static final BlockWithItem NETHER_INFUSED_POWERED_RAIL = BlockWithItem.register("nether_infused_powered_rail", () -> new NetherInfusedRailBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.NETHER_BRICKS)));

	public Minecarts(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}