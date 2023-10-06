package insane96mcp.survivalreimagined.module.experience.enchanting;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Enchanting", description = "Change some enchanting related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantingFeature extends Feature {
	public static final SimpleBlockWithItem ENCHANTING_TABLE = SimpleBlockWithItem.register("enchanting_table", () -> new SREnchantingTable(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE).strength(3.5f)));
	public static final RegistryObject<BlockEntityType<SREnchantingTableBlockEntity>> ENCHANTING_TABLE_BLOCK_ENTITY = SRRegistries.BLOCK_ENTITY_TYPES.register("enchanting_table", () -> BlockEntityType.Builder.of(SREnchantingTableBlockEntity::new, ENCHANTING_TABLE.block().get()).build(null));
	public static final RegistryObject<MenuType<SREnchantingTableMenu>> ENCHANTING_TABLE_MENU_TYPE = SRRegistries.MENU_TYPES.register("enchanting_table", () -> new MenuType<>(SREnchantingTableMenu::new, FeatureFlags.VANILLA_SET));

	public EnchantingFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}