package insane96mcp.iguanatweaksreborn.module.items.crate;

import insane96mcp.iguanatweaksreborn.data.criterion.OverweightCrateCarryTrigger;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.SRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

@Label(name = "Crate", description = "A new block that can let you carry more stuff around.")
@LoadFeature(module = Modules.Ids.ITEMS)
public class Crate extends Feature {

	public static final UUID CRATE_WEIGHT_UUID = UUID.fromString("4ce89c45-a011-43fa-b9a8-7f2bd0ea2fc3");

	public static final RegistryObject<CrateBlock> BLOCK = SRRegistries.BLOCKS.register("crate", () -> new CrateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.5f)));
	public static final RegistryObject<CrateItem> ITEM = SRRegistries.ITEMS.register("crate", () -> new CrateItem(BLOCK.get(), new Item.Properties().stacksTo(1)));

	public static final RegistryObject<BlockEntityType<?>> BLOCK_ENTITY_TYPE = SRRegistries.BLOCK_ENTITY_TYPES.register("crate", () -> BlockEntityType.Builder.of(CrateBlockEntity::new, BLOCK.get()).build(null));

	@Config(min = 0)
	@Label(name = "Slowness with this or more crates")
	public static Integer slownessAtCrates = 2;

	@Config(min = 0d, max = 1d)
	@Label(name = "Slowness per crate > 'Slowness with this or more crates'")
	public static Double slownessPerCrate = 0.15d;

	public Crate(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| slownessPerCrate == 0d
				|| event.phase == TickEvent.Phase.START)
			return;

		int cratesInInventory = ContainerHelper.clearOrCountMatchingItems(event.player.getInventory(), stack -> stack.is(ITEM.get()), 0, true);
		event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(CRATE_WEIGHT_UUID);
		if (cratesInInventory >= slownessAtCrates) {
			double slowness = (cratesInInventory - (slownessAtCrates - 1)) * slownessPerCrate;
			MCUtils.applyModifier(event.player, Attributes.MOVEMENT_SPEED, CRATE_WEIGHT_UUID, "Crate weight penalty", -slowness, AttributeModifier.Operation.MULTIPLY_BASE, false);
			if (event.player.tickCount % 20 == 4 && event.player instanceof ServerPlayer serverPlayer)
				OverweightCrateCarryTrigger.TRIGGER.trigger(serverPlayer);
		}
	}
}