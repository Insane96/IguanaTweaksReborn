package insane96mcp.survivalreimagined.module.misc.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Tweaks", description = "Various stuff that doesn't fit in any other Feature.")
@LoadFeature(module = Modules.Ids.MISC)
public class Tweaks extends Feature {

    @Config
    @Label(name = "Prevent fire with resistance", description = "If true, entities will no longer be set on fire if have Fire Resistance (like bedrock edition)")
    public static Boolean preventFireWithResistance = true;

    @Config
    @Label(name = "Less burn time for Kelp block", description = "Kelp blocks smelt 16 items instead of 20")
    public static Boolean lessBurnTimeForKelpBlock = true;

    @Config
    @Label(name = "Falling breaking glass", description = "Falling on glass has a chance of breaking it. The higher the fall, the higher the chance")
    public static Boolean fallingBreakingGlass = true;
    @Config
    @Label(name = "Slower poison", description = "If true, poison will damage the player every 80 ticks at level I instead of 25.")
    public static Boolean slowerPoison = true;
    //TODO Remove when quark 1.20.1
    @Config
    @Label(name = "Maximum Sponge Soak Blocks", description = "The maximum amount of blocks a sponge can soak. (Vanilla is 64)")
    public static Integer maxSpongeSoakBlocks = 256;
    @Config
    @Label(name = "Maximum Sponge Soak Range", description = "The maximum range at which sponges will check for soakable blocks. (Vanilla is 5)")
    public static Integer maxSpongeSoakRange = 10;

    public Tweaks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static boolean isFireImmune(Entity entity) {
        if (!isEnabled(Tweaks.class)
                || !preventFireWithResistance
                || !(entity instanceof LivingEntity livingEntity))
            return false;

       return livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE);
    }

    public static int changeMaxSpongeSoakBlocks(int soakableBlocks) {
        if (!isEnabled(Tweaks.class))
            return soakableBlocks;

        //Vanilla uses 65 and not 64
        return maxSpongeSoakBlocks + 1;
    }

    public static int changeSpongeMaxRange(int range) {
        if (!isEnabled(Tweaks.class))
            return range;

        //Vanilla uses 65 and not 64
        return maxSpongeSoakRange + 1;
    }

    public static boolean isSlowerPoison() {
        return isEnabled(Tweaks.class) && slowerPoison;
    }

    @SubscribeEvent
    public void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (!this.isEnabled()
                || !lessBurnTimeForKelpBlock
                || !event.getItemStack().is(Items.DRIED_KELP_BLOCK))
            return;

        event.setBurnTime(3200);
    }

    @SubscribeEvent
    public void onFalling(LivingFallEvent event) {
        if (!this.isEnabled()
                || !fallingBreakingGlass
                || event.getEntity().level().isClientSide)
            return;

        LivingEntity entity = event.getEntity();
        AABB bb = entity.getBoundingBox();
        int mX = Mth.floor(bb.minX);
        int mZ = Mth.floor(bb.minZ);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        float chance = (event.getDistance() - 3) * 0.05f;
        if (entity.getRandom().nextFloat() >= chance)
            return;
        for (int x2 = mX; x2 < bb.maxX; x2++) {
            for (int z2 = mZ; z2 < bb.maxZ; z2++) {
                pos.set(x2, entity.position().y - 1.0E-5F, z2);
                BlockState state = entity.level().getBlockState(pos);
                if (state.is(Tags.Blocks.GLASS)) {
                    BlockEntity blockEntity = state.hasBlockEntity() ? entity.level().getBlockEntity(pos) : null;
                    LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel) entity.level())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, entity);
                    state.getDrops(lootcontext$builder).forEach(stack ->
                        entity.level().addFreshEntity(new ItemEntity(entity.level(), pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack))
                    );
                    entity.level().destroyBlock(pos, false);
                }
            }
        }
    }
}
