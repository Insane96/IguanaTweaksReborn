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
import insane96mcp.survivalreimagined.data.lootmodifier.ReplaceDropModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Flint Tools", description = "Add flint tools and make wooden tools useless")
@LoadFeature(module = Modules.Ids.ITEMS)
public class FlintTools extends Feature {

	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("flint", 3d, 33, () -> Items.FLINT, 9, Rarity.COMMON);

	private static final ILItemTier ITEM_TIER = new ILItemTier(1, 99, 6f, 1.5f, 9, () -> Ingredient.of(Items.FLINT));

	public static final RegistryObject<Item> SWORD = SRItems.REGISTRY.register("flint_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRItems.REGISTRY.register("flint_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRItems.REGISTRY.register("flint_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRItems.REGISTRY.register("flint_axe", () -> new AxeItem(ITEM_TIER, 7.0F, -3.1F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRItems.REGISTRY.register("flint_hoe", () -> new HoeItem(ITEM_TIER, -1, -2.0F, new Item.Properties()));

	public static final RegistryObject<SPShieldItem> SHIELD = SRItems.registerShield("flint_shield", SHIELD_MATERIAL);

	@Config
	@Label(name = "Disable Wooden Tools", description = "Makes wooden items deal no damage and not able to break blocks.")
	public static Boolean disableWoodenTools = true;

	@Config
	@Label(name = "Disable Wooden Tools Recipe", description = "Disable wooden tools recipe.")
	public static Boolean disableWoodenToolsRecipe = true;

	public FlintTools(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "disable_wooden_tools", net.minecraft.network.chat.Component.literal("Survival Reimagined Disable Wooden Tools"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && disableWoodenToolsRecipe));
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

	private static final String path = "flint_tools/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "replace_wooden_sword", new ReplaceDropModifier.Builder(Items.WOODEN_SWORD, SWORD.get())
				.applyToChestsOnly()
				.build()
		);
		provider.add(path + "replace_wooden_axe", new ReplaceDropModifier.Builder(Items.WOODEN_AXE, AXE.get())
				.applyToChestsOnly()
				.build()
		);
		provider.add(path + "replace_wooden_shovel", new ReplaceDropModifier.Builder(Items.WOODEN_SHOVEL, SHOVEL.get())
				.applyToChestsOnly()
				.build()
		);
		provider.add(path + "replace_wooden_pickaxe", new ReplaceDropModifier.Builder(Items.WOODEN_PICKAXE, PICKAXE.get())
				.applyToChestsOnly()
				.build()
		);
		provider.add(path + "replace_wooden_hoe", new ReplaceDropModifier.Builder(Items.WOODEN_HOE, HOE.get())
				.applyToChestsOnly()
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