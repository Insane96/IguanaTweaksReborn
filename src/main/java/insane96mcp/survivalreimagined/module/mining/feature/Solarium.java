package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.event.HurtItemStackEvent;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.item.SRArmorMaterial;
import insane96mcp.survivalreimagined.module.mining.block.SoliumMossBlock;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

@Label(name = "Solarium", description = "Add Solarium, a new metal made by alloying Overgrown solium moss ball (found in hot biomes) and can be used to upgrade Iron Equipment")
@LoadFeature(module = Modules.Ids.MINING)
public class Solarium extends Feature {

	public static final TagKey<Item> SOLARIUM_EQUIPMENT = TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, "equipment/solarium"));

	public static final SimpleBlockWithItem SOLIUM_MOSS = SimpleBlockWithItem.register("solium_moss", () -> new SoliumMossBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.GLOW_LICHEN).noCollission().strength(0.4F).sound(SoundType.GLOW_LICHEN).lightLevel(GlowLichenBlock.emission(9))));
	public static final RegistryObject<Item> SOLARIUM_BALL = SRItems.REGISTRY.register("solarium_ball", () -> new Item(new Item.Properties()));

	public static final ILItemTier ITEM_TIER = new ILItemTier(2, 307, 5.5f, 1.5f, 12, () -> Ingredient.of(SOLARIUM_BALL.get()));

	public static final RegistryObject<Item> SWORD = SRItems.REGISTRY.register("solarium_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRItems.REGISTRY.register("solarium_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRItems.REGISTRY.register("solarium_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRItems.REGISTRY.register("solarium_axe", () -> new AxeItem(ITEM_TIER, 5.5F, -3.2F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRItems.REGISTRY.register("solarium_hoe", () -> new HoeItem(ITEM_TIER, -2, -1.0F, new Item.Properties()));

	private static final SRArmorMaterial ARMOR_MATERIAL = new SRArmorMaterial("survivalreimagined:solarium", 16, Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
		p_266652_.put(ArmorItem.Type.BOOTS, 2);
		p_266652_.put(ArmorItem.Type.LEGGINGS, 5);
		p_266652_.put(ArmorItem.Type.CHESTPLATE, 6);
		p_266652_.put(ArmorItem.Type.HELMET, 2);
	}), 12, SoundEvents.ARMOR_EQUIP_IRON, 0f, 0f, () -> Ingredient.of(SOLARIUM_BALL.get()));

	public static final RegistryObject<Item> HELMET = SRItems.REGISTRY.register("solarium_helmet", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> CHESTPLATE = SRItems.REGISTRY.register("solarium_chestplate", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> LEGGINGS = SRItems.REGISTRY.register("solarium_leggings", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> BOOTS = SRItems.REGISTRY.register("solarium_boots", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("solarium", 4.5d, 401, SOLARIUM_BALL, 8, Rarity.COMMON);

	public static final RegistryObject<SPShieldItem> SHIELD = SRItems.registerShield("solarium_shield", SHIELD_MATERIAL);

	public Solarium(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !event.getItemStack().is(SOLARIUM_EQUIPMENT))
			return;

		event.getToolTip().add(Component.empty());
		event.getToolTip().add(Component.translatable("survivalreimagined:innate_solarium").withStyle(ChatFormatting.GREEN));
	}

	@SubscribeEvent
	public void onHurtItemStack(HurtItemStackEvent event) {
		if (!this.isEnabled()
				|| !event.getStack().is(SOLARIUM_EQUIPMENT)
				|| event.getPlayer() == null
				|| !event.getPlayer().getLevel().canSeeSky(event.getPlayer().blockPosition()))
			return;

		int skyLight = event.getPlayer().getLevel().getBrightness(LightLayer.SKY, event.getPlayer().blockPosition());
		if (event.getPlayer().getLevel().getDayTime() % 24000 > 12542)
			skyLight /= 2;
		if (event.getPlayer().getLevel().isRaining())
			skyLight /= 2;
		if (event.getPlayer().getLevel().isThundering())
			skyLight /= 2;
		int amount = event.getAmount();
		int newAmount = 0;
		for(int i = 0; i < amount; i++) {
			//5% per light level (75% at 15,
			if (event.getRandom().nextFloat() >= skyLight * 0.05f)
				++newAmount;
		}
		event.setAmount(newAmount);
	}

	@SubscribeEvent
	public void onBlockBreak(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| !event.getState().is(SOLIUM_MOSS.block().get())
				|| !(event.getEntity().getMainHandItem().getItem() instanceof ShearsItem))
			return;

		event.setNewSpeed(event.getOriginalSpeed() * 5f);
	}
}