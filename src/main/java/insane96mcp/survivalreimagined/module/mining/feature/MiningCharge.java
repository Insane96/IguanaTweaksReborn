package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.mining.block.MiningChargeBlock;
import insane96mcp.survivalreimagined.module.mining.entity.PrimedMiningCharge;
import insane96mcp.survivalreimagined.setup.SREntityTypes;
import insane96mcp.survivalreimagined.setup.SRSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Mining Charge", description = "Adds mining charge")
@LoadFeature(module = Modules.Ids.MINING)
public class MiningCharge extends Feature {

	public static final BlockWithItem MINING_CHARGE = BlockWithItem.register("mining_charge", () -> new MiningChargeBlock(BlockBehaviour.Properties.copy(Blocks.TNT)));

	public static final RegistryObject<EntityType<PrimedMiningCharge>> PRIMED_MINING_CHARGE = SREntityTypes.REGISTRY.register("mining_charge", () -> EntityType.Builder.<PrimedMiningCharge>of(PrimedMiningCharge::new, MobCategory.MISC).fireImmune().sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10).build("mining_charge"));

	public static final RegistryObject<SoundEvent> PRIMED_MINING_CHARGE_SOUND = SRSoundEvents.REGISTRY.register("primed_mining_charge", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation("entity.tnt.primed"), 16f));

	public MiningCharge(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}
