package insane96mcp.survivalreimagined.module.experience.enchanting;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRBlockEntityTypes;
import insane96mcp.survivalreimagined.setup.SRMenuType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
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
	@Label(name = "Nether Enchanting Table", description = "Enables a data pack that makes the enchanting table require nether access to be made")
	public static Boolean netherEnchantingTable = true;

	@Config(min = 1, max = 3)
	@Label(name = "Ensorceller.Base Roll Cost")
	public static Integer ensorcellerBaseRollCost = 3;
	@Config(min = 1)
	@Label(name = "Ensorceller.Bookshelf amount to lower level")
	public static Integer ensorcellerBookshelfToLowerLevel = 4;
	@Config
	@Label(name = "Ensorceller.No Merge", description = "Items enchanted in an ensorceller cannot be merged")
	public static Boolean ensorcellerNoMerge = true;

	public static final SimpleBlockWithItem ENSORCELLER = SimpleBlockWithItem.register("ensorceller", () -> new EnsorcellerBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE).strength(3.5f)));
	public static final RegistryObject<BlockEntityType<EnsorcellerBlockEntity>> ENSORCELLER_BLOCK_ENTITY_TYPE = SRBlockEntityTypes.REGISTRY.register("ensorceller", () -> BlockEntityType.Builder.of(EnsorcellerBlockEntity::new, ENSORCELLER.block().get()).build(null));
	public static final RegistryObject<MenuType<EnsorcellerMenu>> ENSORCELLER_MENU_TYPE = SRMenuType.REGISTRY.register("ensorceller", () -> new MenuType<>(EnsorcellerMenu::new, FeatureFlags.VANILLA_SET));

	public EnchantingFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "nether_enchanting_table", Component.literal("Survival Reimagined Nether Enchanting Table"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && netherEnchantingTable));
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled())
			return;

		Minecraft mc = Minecraft.getInstance();
		if (!(mc.screen instanceof AnvilScreen) && !(mc.screen instanceof EnsorcellerScreen))
			return;

		//noinspection DataFlowIssue
		if (event.getItemStack().hasTag() && event.getItemStack().getTag().contains(EnsorcellerBlock.CANNOT_MERGE_TAG)) {
			event.getToolTip().add(Component.translatable(EnsorcellerBlock.CANNOT_BE_MERGED_LANG).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
		}
	}
}