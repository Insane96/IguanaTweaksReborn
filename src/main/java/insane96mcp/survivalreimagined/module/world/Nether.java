package insane96mcp.survivalreimagined.module.world;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableInt;

@Label(name = "Nether")
@LoadFeature(module = Modules.Ids.WORLD)
public class Nether extends Feature {
    public static final String REQUIRES_CORNERS_LANG = SurvivalReimagined.MOD_ID + ".requires_corners";

    @Config
    @Label(name = "Disable Nether Roof and 8 block ratio", description = "Makes the nether 128 blocks high instead of 256, effectively disabling the \"Nether Roof\" and removes the 8 block ratio between nether and end.")
    public static Boolean disableNetherRoof = true;
    @Config
    @Label(name = "Portal requires Gold Blocks or Crying Obsidians", description = "The portal requires Gold blocks or Crying Obsidians in the corners to turn it on (in the overworld).")
    public static Boolean portalRequiresGoldBlock = true;
    @Config
    @Label(name = "Remove Lava Pockets", description = "If true, lava pockets in the nether are removed.")
    public static Boolean removeLavaPockets = true;

    public Nether(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "no_nether_roof", Component.literal("Survival Reimagined No Nether Roof"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && disableNetherRoof));
    }

    public static boolean shouldDisableLavaPockets(SpringConfiguration configuration) {
        return isEnabled(Nether.class) && removeLavaPockets && !configuration.requiresBlockBelow && configuration.state.is(FluidTags.LAVA);
    }

    @SubscribeEvent
    public void onPortalTryToActivate(BlockEvent.PortalSpawnEvent event) {
        if (!this.isEnabled()
                || !portalRequiresGoldBlock
                || event.getPortalSize().bottomLeft == null)
            return;

        Level level = (Level) event.getLevel();
        if (!level.dimension().equals(Level.OVERWORLD))
            return;

        MutableInt diamondBlocks = new MutableInt();
        BlockPos.betweenClosed(event.getPortalSize().bottomLeft.below().relative(event.getPortalSize().rightDir.getOpposite(), 1),
                event.getPortalSize().bottomLeft.relative(Direction.UP, event.getPortalSize().height).relative(event.getPortalSize().rightDir, event.getPortalSize().width))
                .forEach((pos) -> {
            if (level.getBlockState(pos).is(Blocks.GOLD_BLOCK) || level.getBlockState(pos).is(Blocks.CRYING_OBSIDIAN))
                diamondBlocks.increment();
        });
        if (diamondBlocks.getValue() < 4) {
            AABB aabb = new AABB(event.getPos().offset(-4, -4, -4), event.getPos().offset(4, 4, 4));

            for(Player player : level.players()) {
                if (aabb.contains(player.getX(), player.getY(), player.getZ())) {
                    player.sendSystemMessage(Component.translatable(REQUIRES_CORNERS_LANG));
                }
            }
            event.setCanceled(true);
        }
    }

    /*@SubscribeEvent
    public void onTryEnteringNether(EntityTravelToDimensionEvent event) {
        if (!this.isEnabled()
                || !requireDiamondPickAdvancement
                || !(event.getEntity() instanceof ServerPlayer player))
            return;

        if (player.getAdvancements().getOrStartProgress())
    }*/
}
