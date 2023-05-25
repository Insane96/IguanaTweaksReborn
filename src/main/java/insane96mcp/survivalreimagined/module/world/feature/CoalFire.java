package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.data.lootmodifier.ReplaceLootModifier;
import insane96mcp.survivalreimagined.event.BlockBurntEvent;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.module.world.block.HellishCoalOreBlock;
import insane96mcp.survivalreimagined.module.world.block.PilableLayerBlock;
import insane96mcp.survivalreimagined.module.world.entity.PilableFallingLayerEntity;
import insane96mcp.survivalreimagined.module.world.item.FirestarterItem;
import insane96mcp.survivalreimagined.module.world.item.SimpleFuelItem;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Coal & Fire")
@LoadFeature(module = Modules.Ids.WORLD)
public class CoalFire extends Feature {

    public static final BlockWithItem CHARCOAL_LAYER = BlockWithItem.register("charcoal_layer", () -> new PilableLayerBlock(BlockBehaviour.Properties.of(Material.MOSS, MaterialColor.COLOR_BLACK).strength(0.5F).sound(SoundType.MOSS_CARPET).isViewBlocking((state, blockGetter, pos) -> state.getValue(PilableLayerBlock.LAYERS) >= 8), Items.CHARCOAL));

    public static final RegistryObject<Item> FIRESTARTER = SRItems.REGISTRY.register("firestarter", () -> new FirestarterItem(new Item.Properties().stacksTo(1).defaultDurability(11)));

    public static final BlockWithItem SOUL_SAND_HELLISH_COAL_ORE = BlockWithItem.register("soul_sand_hellish_coal_ore", () -> new HellishCoalOreBlock(BlockBehaviour.Properties.copy(Blocks.SOUL_SAND).strength(2f).sound(SoundType.SOUL_SAND), UniformInt.of(1, 3)));
    public static final BlockWithItem SOUL_SOIL_HELLISH_COAL_ORE = BlockWithItem.register("soul_soil_hellish_coal_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.SOUL_SOIL).strength(2f).sound(SoundType.SOUL_SOIL), UniformInt.of(1, 3)));
    public static final RegistryObject<Item> HELLISH_COAL = SRItems.REGISTRY.register("hellish_coal", () -> new SimpleFuelItem(new Item.Properties().fireResistant(), 2400));

    @Config(min = 0d, max = 100)
    @Label(name = "Fire spread speed multiplier", description = "How much faster fire ticks and spreads.")
    public static Double fireSpreadSpeedMultiplier = 4d;

    @Config(min = 0d, max = 1d)
    @Label(name = "Charcoal from burnt logs chance", description = "Chance for logs to release charcoal layer when burnt")
    public static Double charcoalFromBurntLogsChance = 0.8d;

    @Config
    @Label(name = "Disable Charcoal Smelting", description = "If enabled, a data pack will be enabled that removes the Charcoal recipe from smelting")
    public static Boolean disableCharcoalSmelting = true;
    @Config
    @Label(name = "\"Iron\" Coal", description = "If enabled coal cannot be mined without an Iron Pickaxe or higher.")
    public static Boolean ironCoal = true;

    @Config
    @Label(name = "Two flint fire starter.Enabled", description = "If true, two flints (on per hand) can start a fire")
    public static Boolean twoFlintFireStarter = true;
    @Config(min = 0d, max = 1d)
    @Label(name = "Two flint fire starter.Ignite Chance", description = "Chance to ignite a block when using two flints")
    public static Double twoFlintFireStarterIgniteChance = 0.35d;
    @Config(min = 0d, max = 1d)
    @Label(name = "Two flint fire starter.Break Chance", description = "Chance for the flint to break when using two flints")
    public static Double twoFlintFireStarterBreakChance = 0.3d;
    @Config
    @Label(name = "Unlit campfire", description = "If true, campfires must be lit")
    public static Boolean unlitCampfires = true;

    public CoalFire(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "no_charcoal_smelting", Component.literal("Survival Reimagined No Charcoal Smelting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && disableCharcoalSmelting));
        Blocks.CAMPFIRE.defaultBlockState = Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, Boolean.FALSE);
        Blocks.SOUL_CAMPFIRE.defaultBlockState = Blocks.SOUL_CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, Boolean.FALSE);
    }

    @SubscribeEvent
    public void onBlockBurnt(BlockBurntEvent event) {
        if (!this.isEnabled()
                || charcoalFromBurntLogsChance == 0d)
            return;

        if (event.getLevel().getRandom().nextDouble() < charcoalFromBurntLogsChance
                && event.getState().is(BlockTags.LOGS_THAT_BURN)) {
            PilableFallingLayerEntity.fall((Level) event.getLevel(), event.getPos(), CHARCOAL_LAYER.block().get().defaultBlockState());
        }
    }

    @SubscribeEvent
    public void onBlockRightClicked(PlayerInteractEvent.RightClickBlock event) {
        if (!this.isEnabled()
                || !twoFlintFireStarter
                || event.getHand() != InteractionHand.MAIN_HAND
                || !event.getItemStack().is(Items.FLINT)
                || !event.getEntity().getOffhandItem().is(Items.FLINT)
                || event.getEntity().getCooldowns().isOnCooldown(Items.FLINT)
                || event.getLevel().isClientSide)
            return;

        double breakMain = event.getEntity().getRandom().nextDouble();
        double breakOff = event.getEntity().getRandom().nextDouble();
        double ignite = event.getEntity().getRandom().nextDouble();

        event.getEntity().swing(event.getHand(), true);
        event.setCanceled(true);
        event.getEntity().getCooldowns().addCooldown(event.getItemStack().getItem(), 15);

        if (ignite < twoFlintFireStarterIgniteChance) {
            UseOnContext context = new UseOnContext(event.getEntity(), event.getHand(), new BlockHitResult(event.getHitVec().getLocation(), event.getHitVec().getDirection(), event.getPos(), event.getHitVec().isInside()));
            //Yes, I copy-pasted FlintAndSteelItem#use
            Player player = context.getPlayer();
            Level level = context.getLevel();
            BlockPos blockpos = context.getClickedPos();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
                BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
                if (BaseFireBlock.canBePlacedAt(level, blockpos1, context.getHorizontalDirection())) {
                    BlockState blockstate1 = BaseFireBlock.getState(level, blockpos1);
                    level.setBlock(blockpos1, blockstate1, 11);
                    level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                }
            }
            else {
                level.setBlock(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockpos);
            }
            event.getLevel().playSound(null, event.getPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, event.getLevel().getRandom().nextFloat() * 0.4F + 0.8F);
        }
        //On fail, play a high-pitched sound
        else {
            event.getLevel().playSound(null, event.getPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, event.getLevel().getRandom().nextFloat() * 0.4F + 1.5F);
        }

        if (breakMain < twoFlintFireStarterBreakChance) {
            event.getItemStack().shrink(1);
            event.getEntity().broadcastBreakEvent(event.getHand());
            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(event.getEntity(), event.getItemStack(), event.getHand());
        }
        if (breakOff < twoFlintFireStarterBreakChance) {
            event.getEntity().getOffhandItem().shrink(1);
            event.getEntity().broadcastBreakEvent(InteractionHand.OFF_HAND);
            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(event.getEntity(), event.getEntity().getOffhandItem(), InteractionHand.OFF_HAND);
        }
    }

    @SubscribeEvent
    public void onCharcoalRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!this.isEnabled()
                || !event.getItemStack().is(Items.CHARCOAL)
                || event.getCancellationResult().consumesAction())
            return;

        UseOnContext context = new UseOnContext(event.getLevel(), event.getEntity(), event.getHand(), event.getItemStack(), event.getHitVec());
        InteractionResult result = CHARCOAL_LAYER.item().get().useOn(context);
        if (result.shouldSwing()) {
            event.getEntity().swing(event.getHand());
        }
    }

    public static boolean changeFireSpreadSpeed() {
        return Feature.isEnabled(CoalFire.class) && fireSpreadSpeedMultiplier != 1d;
    }

    public static boolean areCampfiresUnlit() {
        return Feature.isEnabled(CoalFire.class) && unlitCampfires;
    }

    @SubscribeEvent
    public void onCoalMine(PlayerEvent.BreakSpeed event) {
        if (!this.isEnabled()
                || !ironCoal
                || !(event.getState().is(Blocks.COAL_ORE) || event.getState().is(Blocks.DEEPSLATE_COAL_ORE))
                || !(event.getEntity().getMainHandItem().getItem() instanceof TieredItem tieredItem)
                || tieredItem.getTier().getLevel() >= 2)
            return;

        event.setNewSpeed(event.getNewSpeed() / 5f);
    }

    private static final String path = "coal_fire/";

    public static void addGlobalLoot(GlobalLootModifierProvider provider) {
        provider.add(path + "replace_flint_and_steel", new ReplaceLootModifier.Builder(new LootItemCondition[] {
                    LocationCheck.checkLocation(new LocationPredicate.Builder().setDimension(Level.OVERWORLD)).build()
                }, Items.FLINT_AND_STEEL, FIRESTARTER.get())
                .applyToChestsOnly()
                .keepDurability()
                .build()
        );
    }
}