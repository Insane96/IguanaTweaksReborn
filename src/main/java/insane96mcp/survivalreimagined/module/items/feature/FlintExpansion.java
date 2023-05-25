package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.data.lootmodifier.ReplaceLootModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.module.world.block.GroundRockBlock;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRItems;
import insane96mcp.survivalreimagined.setup.Strings;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Flint Expansion", description = "Add flint tools and make wooden tools useless. Also add flint blocks.")
@LoadFeature(module = Modules.Ids.ITEMS)
public class FlintExpansion extends Feature {

	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("flint", 3d, 33, () -> Items.FLINT, 9, Rarity.COMMON);

	public static final BlockWithItem FLINT_ROCK = BlockWithItem.register("flint_rock", () -> new GroundRockBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(0.5F, 1F).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));

	public static final ILItemTier ITEM_TIER = new ILItemTier(1, 99, 6f, 1f, 9, () -> Ingredient.of(Items.FLINT));

	public static final RegistryObject<Item> SWORD = SRItems.REGISTRY.register("flint_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRItems.REGISTRY.register("flint_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRItems.REGISTRY.register("flint_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRItems.REGISTRY.register("flint_axe", () -> new AxeItem(ITEM_TIER, 7.0F, -3.1F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRItems.REGISTRY.register("flint_hoe", () -> new HoeItem(ITEM_TIER, -1, -2.0F, new Item.Properties()));

	public static final RegistryObject<SPShieldItem> SHIELD = SRItems.registerShield("flint_shield", SHIELD_MATERIAL);
	public static final BlockWithItem FLINT_BLOCK = BlockWithItem.register("flint_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 5.0F)));
	public static final BlockWithItem POLISHED_FLINT_BLOCK = BlockWithItem.register("polished_flint_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 5.0F)));

	@Config
	@Label(name = "Disable Wooden Tools", description = "Makes wooden items deal no damage and not able to break blocks.")
	public static Boolean disableWoodenTools = true;

	@Config
	@Label(name = "Disable Wooden Tools Recipe", description = "Disable wooden tools recipe.")
	public static Boolean disableWoodenToolsRecipe = true;

	public FlintExpansion(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "disable_wooden_tools", Component.literal("Survival Reimagined Disable Wooden Tools"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && disableWoodenToolsRecipe));
	}

	@SubscribeEvent
	public void disableWoodenTools(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
			|| !disableWoodenTools)
			return;

		Player player = event.getEntity();
		if (isWoodenTool(player.getMainHandItem().getItem())) {
			event.setCanceled(true);
			event.getEntity().displayClientMessage(Component.translatable(Strings.Translatable.NO_EFFICIENCY_ITEM), true);
		}
	}

	@SubscribeEvent
	public void processAttackDamage(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !disableWoodenTools)
			return;

		if (isWoodenTool(event.getEntity().getMainHandItem().getItem())) {
			event.setAmount(1f);
			if (event.getEntity() instanceof Player player)
				player.displayClientMessage(Component.translatable(Strings.Translatable.NO_DAMAGE_ITEM), true);
		}
	}

	private static final String path = "flint_expansion/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "replace_wooden_sword", new ReplaceLootModifier.Builder(Items.WOODEN_SWORD, SWORD.get())
				.applyToChestsOnly()
				.keepDurability()
				.build()
		);
		provider.add(path + "replace_wooden_axe", new ReplaceLootModifier.Builder(Items.WOODEN_AXE, AXE.get())
				.applyToChestsOnly()
				.keepDurability()
				.build()
		);
		provider.add(path + "replace_wooden_shovel", new ReplaceLootModifier.Builder(Items.WOODEN_SHOVEL, SHOVEL.get())
				.applyToChestsOnly()
				.keepDurability()
				.build()
		);
		provider.add(path + "replace_wooden_pickaxe", new ReplaceLootModifier.Builder(Items.WOODEN_PICKAXE, PICKAXE.get())
				.applyToChestsOnly()
				.keepDurability()
				.build()
		);
		provider.add(path + "replace_wooden_hoe", new ReplaceLootModifier.Builder(Items.WOODEN_HOE, HOE.get())
				.applyToChestsOnly()
				.keepDurability()
				.build()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !disableWoodenTools)
			return;

		if (isWoodenTool(event.getItemStack().getItem())) {
			event.getToolTip().add(Component.translatable(Strings.Translatable.NO_DAMAGE_ITEM).withStyle(ChatFormatting.RED));
			event.getToolTip().add(Component.translatable(Strings.Translatable.NO_EFFICIENCY_ITEM).withStyle(ChatFormatting.RED));
		}
		else if (event.getItemStack().getItem().equals(Items.WOODEN_SWORD)) {
			event.getToolTip().add(Component.translatable(Strings.Translatable.NO_DAMAGE_ITEM).withStyle(ChatFormatting.RED));
		}
	}

	private static boolean isWoodenTool(Item item) {
		return Utils.isItemInTag(item, new ResourceLocation(SurvivalReimagined.MOD_ID, "equipment/hand/tools/wooden"));
	}
}