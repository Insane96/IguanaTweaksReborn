package insane96mcp.iguanatweaksreborn.modules.sleeprespawn.classutils;


import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

//TODO Remove this and use EffectInstance by using the clone constructor 'new EffectInstance(EffectInstance)'
public class EffectOnWakeUp {
    public ResourceLocation potionId;
    public int duration;
    public int amplifier;

    public EffectOnWakeUp(ResourceLocation potionId, int duration, int amplifier) {
        this.potionId = potionId;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public static EffectOnWakeUp parseLine(String line) {
        String[] split = line.split(",");
        if (split.length != 3) {
            LogHelper.Warn("Invalid line \"%s\" for Effects on WakeUp. Format must be modid:potion_id,duration_ticks,amplifier", line);
            return null;
        }
        if (!NumberUtils.isParsable(split[1])) {
            LogHelper.Warn(String.format("Invalid duration \"%s\" for Effects on WakeUp", split[1]));
            return null;
        }
        int duration = Integer.parseInt(split[1]);
        if (!NumberUtils.isParsable(split[2])) {
            LogHelper.Warn(String.format("Invalid amplifier \"%s\" for Effects on WakeUp", split[1]));
            return null;
        }
        int amplifier = Integer.parseInt(split[2]);
        ResourceLocation potion = ResourceLocation.tryCreate(split[0]);
        if (potion == null) {
            LogHelper.Warn("%s potion for Effects on WakeUp is not valid", line);
            return null;
        }
        if (!ForgeRegistries.POTIONS.containsKey(potion)) {
            LogHelper.Warn(String.format("%s potion for Effects on WakeUp seems to not exist", line));
            return null;
        }

        return new EffectOnWakeUp(potion, duration, amplifier);

    }
}
