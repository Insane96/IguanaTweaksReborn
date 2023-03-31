package insane96mcp.survivalreimagined.module.experience.enchantment;

import insane96mcp.survivalreimagined.setup.SREnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Expanded extends Enchantment {
    public Expanded() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinCost(int level) {
        return 15 + 20 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 20;
    }

    public boolean checkCompatibility(Enchantment other) {
        return !(other instanceof DiggingEnchantment) && !(other instanceof Blasting) && super.checkCompatibility(other);
    }

    public static void apply(LivingEntity entity, Level level, BlockPos pos, Direction face, BlockState state) {
        int enchLevel = entity.getMainHandItem().getEnchantmentLevel(SREnchantments.EXPANDED.get());
        if (enchLevel == 0)
            return;
        List<BlockPos> minedBlocks = getMinedBlocks(enchLevel, entity, pos, face);
        for (BlockPos minedBlock : minedBlocks) {
            BlockState minedBlockState = level.getBlockState(minedBlock);
            if (minedBlockState.getMaterial() != state.getMaterial())
                continue;
            if (minedBlockState.getDestroySpeed(level, minedBlock) > state.getDestroySpeed(level, pos) + 0.5d)
                continue;
            if (level instanceof ServerLevel) {
                BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(minedBlock) : null;
                LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) level)).withRandom(level.getRandom()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(minedBlock)).withParameter(LootContextParams.TOOL, entity.getMainHandItem()).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, entity);
                minedBlockState.getDrops(lootcontext$builder).forEach((stack) -> {
                    ItemEntity drop = new ItemEntity(level, minedBlock.getX() + 0.5d, minedBlock.getY() + 0.5d, minedBlock.getZ() + 0.5d, stack);
                    drop.setDefaultPickUpDelay();
                    level.addFreshEntity(drop);
                });
            }
            level.destroyBlock(minedBlock, false, entity);
        }
    }

    public static List<BlockPos> getMinedBlocks(int level, LivingEntity entity, BlockPos pos, Direction face) {
        List<BlockPos> minedBlocks = new ArrayList<>();

        switch (level) {
            case 1 -> {
                minedBlocks.add(pos.below());
            }
            case 2 -> {
                minedBlocks.add(pos.below());
                minedBlocks.add(pos.above());
            }
            case 3 -> {
                minedBlocks.add(pos.below());
                minedBlocks.add(pos.above());
                minedBlocks.add(pos.relative(face.getClockWise()));
                minedBlocks.add(pos.relative(face.getCounterClockWise()));
            }
            case 4 -> {
                //TODO
            }
        }

        return minedBlocks;
    }
}
