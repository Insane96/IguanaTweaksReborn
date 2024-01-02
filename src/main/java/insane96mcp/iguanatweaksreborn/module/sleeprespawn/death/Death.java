package insane96mcp.iguanatweaksreborn.module.sleeprespawn.death;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.death.integration.ToolBelt;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.setup.registry.SimpleBlockWithItem;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.BlockPos;
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

@Label(name = "Death", description = "Changes to death")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Death extends Feature {

	public static final SimpleBlockWithItem GRAVE = SimpleBlockWithItem.register("grave", () -> new GraveBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn().strength(1.5F, 6.0F)));
	public static final RegistryObject<BlockEntityType<?>> GRAVE_BLOCK_ENTITY_TYPE = ITRRegistries.BLOCK_ENTITY_TYPES.register("grave", () -> BlockEntityType.Builder.of(GraveBlockEntity::new, GRAVE.block().get()).build(null));

	//TODO Configs for the grave

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

		BlockPos pos = player.blockPosition();
		if (pos.getY() < player.level().getMinBuildHeight())
			pos = pos.atY(player.level().getMinBuildHeight() + 1);
		if (player.level().getBlockState(pos.below()).canBeReplaced())
			player.level().setBlock(pos.below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
		BlockState grave = GRAVE.block().get().defaultBlockState();
		if (player.level().getFluidState(pos).getType() == Fluids.WATER)
			grave = grave.setValue(GraveBlock.WATERLOGGED, true);
		player.level().destroyBlock(pos, true, player);
		player.level().setBlock(pos, grave, 3);
		GraveBlockEntity graveBlockEntity = (GraveBlockEntity) player.level().getBlockEntity(pos);
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
		/*int xpDropped = PlayerExperience.getExperienceOnDeath(player, true);
		graveBlockEntity.setXpStored(xpDropped);
		player.setExperienceLevels(0);
		player.setExperiencePoints(0);*/
		graveBlockEntity.setOwner(player.getUUID());
		graveBlockEntity.setDeathNumber(player.getStats().getValue(Stats.CUSTOM.get(Stats.DEATHS)) + 1);
		player.getInventory().clearContent();
	}
}