package insane96mcp.survivalreimagined.module.experience.enchanting;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Enchanting", description = "Change some enchanting related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantingFeature extends Feature {

	@Config
	@Label(name = "Ensorceller.No Merge", description = "Items enchanted in an ensorceller cannot be merged")
	public static Boolean ensorcellerNoMerge = true;

	public static final SimpleBlockWithItem ENCHANTING_TABLE = SimpleBlockWithItem.register("enchanting_table", () -> new SREnchantingTable(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE).strength(3.5f)));
	public static final RegistryObject<BlockEntityType<SREnchantingTableBlockEntity>> ENSORCELLER_BLOCK_ENTITY_TYPE = SRRegistries.BLOCK_ENTITY_TYPES.register("enchanting_table", () -> BlockEntityType.Builder.of(SREnchantingTableBlockEntity::new, ENCHANTING_TABLE.block().get()).build(null));
	public static final RegistryObject<MenuType<SREnchantingTableMenu>> ENCHANTING_TABLE_MENU_TYPE = SRRegistries.MENU_TYPES.register("enchanting_table", () -> new MenuType<>(SREnchantingTableMenu::new, FeatureFlags.VANILLA_SET));

	public EnchantingFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled())
			return;

		Minecraft mc = Minecraft.getInstance();
		if (!(mc.screen instanceof AnvilScreen) && !(mc.screen instanceof SREnchantingTableScreen))
			return;

		//noinspection DataFlowIssue
		if (event.getItemStack().hasTag() && event.getItemStack().getTag().contains(SREnchantingTable.CANNOT_MERGE_TAG)) {
			event.getToolTip().add(Component.translatable(SREnchantingTable.CANNOT_BE_MERGED_LANG).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
		}
	}
}