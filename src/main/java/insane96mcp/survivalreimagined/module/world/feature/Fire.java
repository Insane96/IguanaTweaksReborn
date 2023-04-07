package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.event.BlockBurntEvent;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Fire")
@LoadFeature(module = Modules.Ids.WORLD)
public class Fire extends Feature {

    @Config(min = 0d, max = 100)
    @Label(name = "Fire spread speed multiplier", description = "How much faster fire ticks and spreads.")
    public static Double fireSpreadSpeedMultiplier = 4d;

    @Config(min = 0d, max = 1d)
    @Label(name = "Charcoal from burnt logs chance", description = "Chance for logs to release charcoal when burnt")
    public static Double charcoalFromBurntLogsChance = 0.25d;

    @Config
    @Label(name = "Two flint fire starter.Enabled", description = "If true, two flints (on per hand) can start a fire")
    public static Boolean twoFlintFireStarter = true;
    @Config(min = 0d, max = 1d)
    @Label(name = "Two flint fire starter.Ignite Chance", description = "Chance to ignite a block when using two flints")
    public static Double twoFlintFireStarterIgniteChance = 0.4d;
    @Config(min = 0d, max = 1d)
    @Label(name = "Two flint fire starter.Break Chance", description = "Chance for the flint to break when using two flints")
    public static Double twoFlintFireStarterBreakChance = 0.2d;

    public Fire(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onBlockBurnt(BlockBurntEvent event) {
        if (!this.isEnabled())
            return;

        if (charcoalFromBurntLogsChance > 0d) {
            if (event.getState().is(BlockTags.LOGS_THAT_BURN)) {
                ItemEntity item = new ItemEntity((Level) event.getLevel(), event.getPos().getX() + 0.5d, event.getPos().getY() + 0.5d, event.getPos().getZ() + 0.5d, new ItemStack(Items.CHARCOAL));
                item.setDefaultPickUpDelay();
                event.getLevel().addFreshEntity(item);
            }
        }
    }

    @SubscribeEvent
    public void onBlockRightclicked(PlayerInteractEvent.RightClickBlock event) {
        if (!this.isEnabled()
                || !twoFlintFireStarter
                || event.getHand() != InteractionHand.MAIN_HAND
                || !event.getItemStack().is(Items.FLINT)
                || !event.getEntity().getOffhandItem().is(Items.FLINT)
                || event.getLevel().isClientSide)
            return;

        double breakMain = event.getEntity().getRandom().nextDouble();
        double breakOff = event.getEntity().getRandom().nextDouble();
        double ignite = event.getEntity().getRandom().nextDouble();

        event.getEntity().swing(event.getHand(), true);
        event.setCanceled(true);
        event.getEntity().getCooldowns().addCooldown(event.getItemStack().getItem(), 10);

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

    public static boolean changeFireSpreadSpeed() {
        return Feature.isEnabled(Fire.class) && fireSpreadSpeedMultiplier != 1d;
    }
}
