package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.block.ExplosiveBarrelBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

@Label(name = "Explosive Barrel", description = "Add a new explosive block.")
@LoadFeature(module = Modules.Ids.ITEMS)
public class ExplosiveBarrel extends Feature {
	public static final SimpleBlockWithItem BLOCK = SimpleBlockWithItem.register("explosive_barrel", () -> new ExplosiveBarrelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));

	public ExplosiveBarrel(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}