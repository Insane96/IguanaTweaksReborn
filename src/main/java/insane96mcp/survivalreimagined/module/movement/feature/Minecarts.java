package insane96mcp.survivalreimagined.module.movement.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRBlocks;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Minecarts")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class Minecarts extends Feature {

	public static final RegistryObject<NetherInfusedRailBlock> NETHER_INFUSED_POWERED_RAIL = SRBlocks.REGISTRY.register("nether_infused_powered_rail", () -> new NetherInfusedRailBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.NETHER_BRICKS)));
	public static final RegistryObject<Item> NETHER_INFUSED_POWERED_RAIL_ITEM = SRItems.REGISTRY.register("nether_infused_powered_rail", () -> new BlockItem(NETHER_INFUSED_POWERED_RAIL.get(), new Item.Properties().rarity(Rarity.COMMON)));

	public Minecarts(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static class NetherInfusedRailBlock extends PoweredRailBlock {
		protected NetherInfusedRailBlock(Properties properties) {
			super(properties, true);
		}

		@Override
		public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
			if (!(cart instanceof MinecartFurnace)) return cart.isInWater() ? 0.50f : 1.00f;
			else return super.getRailMaxSpeed(state, level, pos, cart);
		}
	}
}