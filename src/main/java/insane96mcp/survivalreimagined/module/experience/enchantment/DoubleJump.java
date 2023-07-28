package insane96mcp.survivalreimagined.module.experience.enchantment;

import insane96mcp.survivalreimagined.setup.SREnchantments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

public class DoubleJump extends Enchantment {
    public DoubleJump() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] {EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 30;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 20;
    }

    public boolean checkCompatibility(Enchantment other) {
        if (other instanceof ProtectionEnchantment otherProtection)
            return otherProtection.type != ProtectionEnchantment.Type.FALL;
        else return super.checkCompatibility(other);
    }

    public static boolean extraJump(Player entity) {
        if (entity.onGround()
                || entity.onClimbable()
                || entity.isInWaterOrBubble())
            return false;

        int lvl = EnchantmentHelper.getEnchantmentLevel(SREnchantments.MA_JUMP.get(), entity);
        if (getRemainingJumps(entity, lvl) <= 0)
            return false;

        entity.jumpFromGround();
        RandomSource random = entity.getCommandSenderWorld().getRandom();
        for (int i = 0; i < 4; i++) {
            entity.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, entity.getX() - 0.25f + random.nextFloat() * 0.5f, entity.getY(), entity.getZ() - 0.25f + random.nextFloat() * 0.5f, 0, 0, 0);
        }
        entity.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 0.5f, 2f);
        entity.getPersistentData().putInt("double_jumps", entity.getPersistentData().getInt("double_jumps") + 1);
        //Set fallDistance to 0 to prevent falling sound client-side
        entity.fallDistance = 0f;
        return true;
    }

    public static int getRemainingJumps(Player entity, int lvl) {
        return lvl - entity.getPersistentData().getInt("double_jumps");
    }
}
