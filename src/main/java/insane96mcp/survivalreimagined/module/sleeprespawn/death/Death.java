package insane96mcp.survivalreimagined.module.sleeprespawn.death;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.sleeprespawn.death.integration.ToolBelt;
import insane96mcp.survivalreimagined.setup.SRBlockEntityTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Label(name = "Death", description = "Changes to death")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Death extends Feature {

	public static final SimpleBlockWithItem GRAVE = SimpleBlockWithItem.register("grave", () -> new GraveBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn().strength(1.5F, 6.0F)));
	public static final RegistryObject<BlockEntityType<?>> GRAVE_BLOCK_ENTITY_TYPE = SRBlockEntityTypes.REGISTRY.register("grave", () -> BlockEntityType.Builder.of(GraveBlockEntity::new, GRAVE.block().get()).build(null));

	public static final String XP_TO_DROP = SurvivalReimagined.RESOURCE_PREFIX + "xp_to_drop";


	public static final UUID MOVEMENT_SPEED_BONUS = UUID.fromString("1905c271-160b-4560-9b76-c97b007657a5");
	public static final UUID ATTACK_DAMAGE_BONUS = UUID.fromString("bce0ee20-1358-4c8c-89ee-9446548a284b");
	public static final UUID ATTACK_DAMAGE_XP_BONUS = UUID.fromString("4b0d7d72-30cb-4200-9cc7-0944308b8bae");
	public static final UUID HEALTH_XP_BONUS = UUID.fromString("db05e364-0189-47bb-a6cb-487791c8dcd2");

	public Death(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerDeath(LivingDeathEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof ServerPlayer player)
				|| player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
				|| player.level().isOutsideBuildHeight(player.blockPosition().getY())
				|| (player.getInventory().isEmpty() && player.experienceLevel == 0))
			return;

		if (player.level().getBlockState(player.blockPosition().below()).canBeReplaced())
			player.level().setBlock(player.blockPosition().below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
		BlockState grave = GRAVE.block().get().defaultBlockState();
		if (player.level().getFluidState(player.blockPosition()).getType() == Fluids.WATER)
			grave = grave.setValue(GraveBlock.WATERLOGGED, true);
		player.level().setBlock(player.blockPosition(), grave, 3);
		GraveBlockEntity graveBlockEntity = (GraveBlockEntity) player.level().getBlockEntity(player.blockPosition());
		List<ItemStack> items = new ArrayList<>();
		player.getInventory().items.forEach(itemStack -> {
			if (!itemStack.isEmpty())
				items.add(itemStack);
		});
		player.getInventory().armor.forEach(itemStack -> {
			if (!itemStack.isEmpty())
				items.add(itemStack);
		});
		player.getInventory().offhand.forEach(itemStack -> {
			if (!itemStack.isEmpty())
				items.add(itemStack);
		});
		if (ModList.get().isLoaded("toolbelt"))
			ToolBelt.onDeath(items, player);
		graveBlockEntity.setItems(items);
		//int xpDropped = PlayerExperience.getExperienceOnDeath(player, true);
		//graveBlockEntity.setXpStored(xpDropped);
		graveBlockEntity.setOwner(player.getUUID());
		graveBlockEntity.setDeathNumber(player.getStats().getValue(Stats.CUSTOM.get(Stats.DEATHS)) + 1);
		/*player.setExperienceLevels(0);
		player.setExperiencePoints(0);*/
		player.getInventory().clearContent();
	}
}