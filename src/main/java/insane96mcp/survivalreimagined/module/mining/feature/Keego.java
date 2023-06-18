package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.insanelib.util.ILMobEffect;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.item.SRArmorMaterial;
import insane96mcp.survivalreimagined.module.mining.block.KeegoOreBlock;
import insane96mcp.survivalreimagined.setup.SRItems;
import insane96mcp.survivalreimagined.setup.SRMobEffects;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

@Label(name = "Keego", description = "Add a new Netherite alternative which makes you go fast")
@LoadFeature(module = Modules.Ids.MINING)
public class Keego extends Feature {

	public static final TagKey<Item> KEEGO_TOOL_EQUIPMENT = TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, "equipment/hand/tools/keego"));
	public static final TagKey<Item> KEEGO_HAND_EQUIPMENT = TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, "equipment/hand/keego"));
	public static final TagKey<Item> KEEGO_ARMOR_EQUIPMENT = TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, "equipment/armor/keego"));

	public static final RegistryObject<MobEffect> MOVEMENT_MOMENTUM = SRMobEffects.REGISTRY.register("movement_momentum", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0xFCD373, false).addAttributeModifier(Attributes.MOVEMENT_SPEED, "544cf3ee-676f-4685-aec7-a6b3d64875b0", 0.01d, AttributeModifier.Operation.MULTIPLY_BASE));
	public static final RegistryObject<MobEffect> ATTACK_MOMENTUM = SRMobEffects.REGISTRY.register("attack_momentum", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0xFCD373, false).addAttributeModifier(Attributes.ATTACK_SPEED, "f6fe8408-b88c-4e51-8892-8b20574cfc49", 0.05d, AttributeModifier.Operation.ADDITION));
	public static final RegistryObject<MobEffect> MINING_MOMENTUM = SRMobEffects.REGISTRY.register("mining_momentum", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0xFCD373, false));

	public static final SimpleBlockWithItem ORE = SimpleBlockWithItem.register("keego_ore", () -> new KeegoOreBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK).strength(-1f, 9f), UniformInt.of(10, 15)));
	public static final RegistryObject<Item> SHARD = SRItems.REGISTRY.register("keego_shard", () -> new Item(new Item.Properties()));

	public static final SimpleBlockWithItem BLOCK = SimpleBlockWithItem.register("keego_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 7.0F).sound(SoundType.METAL)));

	public static final RegistryObject<Item> GEM = SRItems.REGISTRY.register("keego", () -> new Item(new Item.Properties()));

	public static final ILItemTier ITEM_TIER = new ILItemTier(2, 570, 6.5f, 2.5f, 11, () -> Ingredient.of(GEM.get()));

	public static final RegistryObject<Item> SWORD = SRItems.REGISTRY.register("keego_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRItems.REGISTRY.register("keego_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRItems.REGISTRY.register("keego_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRItems.REGISTRY.register("keego_axe", () -> new AxeItem(ITEM_TIER, 6.0F, -3.2F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRItems.REGISTRY.register("keego_hoe", () -> new HoeItem(ITEM_TIER, -2, -1.0F, new Item.Properties()));

	private static final SRArmorMaterial ARMOR_MATERIAL = new SRArmorMaterial("survivalreimagined:keego", 20, Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
		p_266652_.put(ArmorItem.Type.BOOTS, 3);
		p_266652_.put(ArmorItem.Type.LEGGINGS, 5);
		p_266652_.put(ArmorItem.Type.CHESTPLATE, 6);
		p_266652_.put(ArmorItem.Type.HELMET, 2);
	}), 6, SoundEvents.ARMOR_EQUIP_IRON, 1f, 0.03f, () -> Ingredient.of(GEM.get()));

	public static final RegistryObject<Item> HELMET = SRItems.REGISTRY.register("keego_helmet", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> CHESTPLATE = SRItems.REGISTRY.register("keego_chestplate", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> LEGGINGS = SRItems.REGISTRY.register("keego_leggings", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> BOOTS = SRItems.REGISTRY.register("keego_boots", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("keego", 5.5d, 452, GEM, 9, Rarity.COMMON);

	public static final RegistryObject<SPShieldItem> SHIELD = SRItems.registerShield("keego_shield", SHIELD_MATERIAL);

	public Keego(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| !event.getEntity().hasEffect(MINING_MOMENTUM.get()))
			return;

		//noinspection DataFlowIssue
		int lvl = event.getEntity().getEffect(MINING_MOMENTUM.get()).getAmplifier() + 1;
		event.setNewSpeed(event.getNewSpeed() * (1 + lvl * 0.05f));
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!this.isEnabled()
				|| !event.getPlayer().getMainHandItem().is(KEEGO_TOOL_EQUIPMENT))
			return;

		int amplifier = 0;
		if (event.getPlayer().hasEffect(MINING_MOMENTUM.get()))
			//noinspection DataFlowIssue
			amplifier = event.getPlayer().getEffect(MINING_MOMENTUM.get()).getAmplifier() + 1;

		int duration = (int) (event.getState().getDestroySpeed(event.getLevel(), event.getPos()) * 20 * GlobalHardness.getBlockHardnessMultiplier(event.getState().getBlock(), ((Level)event.getLevel()).dimension().location(), event.getPos())) * 2;
		event.getPlayer().addEffect(new MobEffectInstance(MINING_MOMENTUM.get(), duration, Math.min(amplifier, 31), false, false, true));
	}

	/*@SubscribeEvent
	public void onMoving(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.player.level.isClientSide
				|| event.player.walkDist - event.player.walkDistO < 0.04f
				|| event.phase == TickEvent.Phase.START
				|| event.player.getAbilities().instabuild)
			return;

		int amplifier = 0;
		if (event.player.hasEffect(MOVEMENT_MOMENTUM.get()))
			//noinspection DataFlowIssue
			amplifier = event.player.getEffect(MOVEMENT_MOMENTUM.get()).getAmplifier() + 1;

		AtomicInteger maxAmplifier = new AtomicInteger();
		event.player.getInventory().armor.forEach(stack -> {
			if (stack.is(KEEGO_ARMOR_EQUIPMENT))
				maxAmplifier.incrementAndGet();
		});

		event.player.addEffect(new MobEffectInstance(MOVEMENT_MOMENTUM.get(), 20, Math.min(amplifier, maxAmplifier.get()), false, false, true));
	}*/

	@SubscribeEvent
	public void onAttack(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof ServerPlayer serverPlayer)
				|| serverPlayer.getAttackStrengthScale(0.5f) <= 0.9f
				|| !serverPlayer.getMainHandItem().is(KEEGO_HAND_EQUIPMENT))
			return;

		int amplifier = 0;
		if (serverPlayer.hasEffect(ATTACK_MOMENTUM.get()))
			//noinspection DataFlowIssue
			amplifier = serverPlayer.getEffect(ATTACK_MOMENTUM.get()).getAmplifier() + 1;

		serverPlayer.addEffect(new MobEffectInstance(ATTACK_MOMENTUM.get(), (int) ((4 - serverPlayer.getAttribute(Attributes.ATTACK_SPEED).getValue()) * 20), Math.min(amplifier, 7), false, false, true));
	}
}