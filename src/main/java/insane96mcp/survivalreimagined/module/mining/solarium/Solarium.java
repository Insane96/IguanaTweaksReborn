package insane96mcp.survivalreimagined.module.mining.solarium;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.event.HurtItemStackEvent;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.mining.solarium.item.*;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Solarium", description = "Add Solarium, a new metal made by alloying Overgrown solium moss ball (found in hot biomes) and can be used to upgrade Iron Equipment")
@LoadFeature(module = Modules.Ids.MINING)
public class Solarium extends Feature {

	public static final TagKey<Item> SOLARIUM_EQUIPMENT = TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, "equipment/solarium"));

	public static final SimpleBlockWithItem SOLIUM_MOSS = SimpleBlockWithItem.register("solium_moss", () -> new SoliumMossBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).pushReaction(PushReaction.DESTROY).noCollission().strength(0.4F).sound(SoundType.GLOW_LICHEN).lightLevel(GlowLichenBlock.emission(9)).randomTicks()));
	public static final RegistryObject<Item> SOLARIUM_BALL = SRItems.REGISTRY.register("solarium_ball", () -> new Item(new Item.Properties()));

	public static final ILItemTier ITEM_TIER = new ILItemTier(2, 307, 5.5f, 1.5f, 12, () -> Ingredient.of(SOLARIUM_BALL.get()));

	public static final RegistryObject<Item> SWORD = SRItems.REGISTRY.register("solarium_sword", () -> new SolariumSwordItem(3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRItems.REGISTRY.register("solarium_shovel", () -> new SolariumShovelItem(1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRItems.REGISTRY.register("solarium_pickaxe", () -> new SolariumPickaxeItem(1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRItems.REGISTRY.register("solarium_axe", () -> new SolariumAxeItem(5.5F, -3.2F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRItems.REGISTRY.register("solarium_hoe", () -> new SolariumHoeItem(-2, -1.0F, new Item.Properties()));

	public static final RegistryObject<Item> HELMET = SRItems.REGISTRY.register("solarium_helmet", () -> new SolariumArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> CHESTPLATE = SRItems.REGISTRY.register("solarium_chestplate", () -> new SolariumArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> LEGGINGS = SRItems.REGISTRY.register("solarium_leggings", () -> new SolariumArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> BOOTS = SRItems.REGISTRY.register("solarium_boots", () -> new SolariumArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final RegistryObject<SPShieldItem> SHIELD = SolariumShield.registerShield("solarium_shield");

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
				|| !event.getPlayer().level().canSeeSky(event.getPlayer().blockPosition()))
			return;

		Level level = event.getPlayer().level();
		int skyLight = level.getBrightness(LightLayer.SKY, event.getPlayer().blockPosition());
		if (level.getDayTime() % 24000 > 12542)
			skyLight /= 2;
		if (level.isRaining())
			skyLight /= 2;
		if (level.isThundering())
			skyLight /= 2;
		int amount = event.getAmount();
		int newAmount = 0;
		for(int i = 0; i < amount; i++) {
			//5% per light level (75% at 15)
			if (event.getRandom().nextFloat() >= skyLight * 0.05f)
				++newAmount;
		}
		event.setAmount(newAmount);
	}

	public static void healGear(ItemStack stack, Entity entity, Level level) {
		if (!(level instanceof ServerLevel)
				|| entity.tickCount % 200 != 22)
			return;
		int skyLight = level.getBrightness(LightLayer.SKY, entity.blockPosition());
		if (level.getDayTime() % 24000 > 12542)
			skyLight /= 2;
		if (level.isRaining())
			skyLight /= 2;
		if (level.isThundering())
			skyLight /= 2;
		float chance = skyLight * 0.05f;
		if (level.random.nextFloat() >= chance)
			return;
		stack.setDamageValue(stack.getDamageValue() - 1);
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