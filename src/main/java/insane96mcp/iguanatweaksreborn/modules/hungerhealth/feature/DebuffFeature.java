package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.classutils.Debuff;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Debuffing", description = "Apply potion effects on certain hunger / health / experience level")
public class DebuffFeature extends ITFeature {
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> debuffsConfig;

    private final List<String> debuffsDefault = Arrays.asList("HUNGER,..2,minecraft:mining_fatigue,0", "HUNGER,..4,minecraft:slowness,0", "HEALTH,..3,minecraft:slowness,0");

    public ArrayList<Debuff> debuffs;

    public DebuffFeature(ITModule module) {
        super(module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        debuffsConfig = Config.builder
                .comment("A list of debuffs to apply to the player when has on low hunger / health. Each string must be 'stat,range,status_effect,amplifier', where stat MUST BE one of the following: HUNGER, HEALTH, EXPERIENCE_LEVEL; range must be a range for the statistic like it's done in commands.\n" +
                        "'10' When the player has exactly ten of the specified stat.\n" +
                        "'10..12' When the player has between 10 and 12 (inclusive) of the specified stat.\n" +
                        "'5..' When the player has five or greater of the specified stat.\n" +
                        "'..15' When the player has 15 or less of the specified stat.\n" +
                        "effect must be a potion id, e.g. minecraft:weakness\n" +
                        "amplifier must be the potion level starting from 0 (0 = level I)\n" +
                        "Thus is called Debuffs, this can be used to give the player positive effects.")
                .defineList("Debuffs", debuffsDefault, o -> o instanceof String);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        debuffs = parseDebuffs(debuffsConfig.get());
    }

    private static ArrayList<Debuff> parseDebuffs(List<? extends String> list) {
        ArrayList<Debuff> debuffs = new ArrayList<>();
        for (String line : list) {
            Debuff debuff = Debuff.parseLine(line);
            if (debuff != null)
                debuffs.add(debuff);
        }

        return debuffs;
    }

    @SubscribeEvent
    public void debuffsOnLowStats(TickEvent.PlayerTickEvent event) {
        if (!this.isEnabled())
            return;

        if (debuffs.isEmpty())
            return;

        if (event.player.world.isRemote())
            return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.player;

        if (player.ticksExisted % 20 != 0)
            return;

        for (Debuff debuff : debuffs) {
            boolean pass = false;
            switch (debuff.stat) {
                case HEALTH:
                    if (player.getHealth() <= debuff.max && player.getHealth() >= debuff.min)
                        pass = true;
                    break;

                case HUNGER:
                    if (player.getFoodStats().getFoodLevel() <= debuff.max && player.getFoodStats().getFoodLevel() >= debuff.min)
                        pass = true;
                    break;

                case EXPERIENCE_LEVEL:
                    if (player.experienceLevel <= debuff.max && player.experienceLevel >= debuff.min)
                        pass = true;
                    break;
                default:
                    break;
            }
            if (pass) {
                EffectInstance effectInstance = new EffectInstance(debuff.effect, 30, debuff.amplifier, true, true, false);
                player.addPotionEffect(effectInstance);
            }
        }
    }
}
