package insane96mcp.iguanatweaksreborn.module.world.weather;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class WeatherSavedData extends SavedData {

    public FoggyData foggyData = new FoggyData(0, -1, Foggy.NONE, Foggy.NONE);
    public ThunderIntensityData thunderIntensityData = new ThunderIntensityData(0, -1, 5);

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        CompoundTag foggyDataTag = new CompoundTag();
        foggyData.save(foggyDataTag);
        compoundTag.put("foggy_data", foggyDataTag);

        CompoundTag thunderIntensityDataTag = new CompoundTag();
        thunderIntensityData.save(thunderIntensityDataTag);
        compoundTag.put("thunder_intensity_data", thunderIntensityDataTag);
        return compoundTag;
    }

    public static WeatherSavedData load(CompoundTag compoundTag) {
        WeatherSavedData weatherSavedData = new WeatherSavedData();
        weatherSavedData.foggyData.load(compoundTag.getCompound("foggy_data"));
        weatherSavedData.thunderIntensityData.load(compoundTag.getCompound("thunder_intensity_data"));
        return weatherSavedData;
    }

    public static WeatherSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(WeatherSavedData::load, WeatherSavedData::new, IguanaTweaksReborn.MOD_ID + "_weather");
    }

    public static final class FoggyData {
        public int timer;
        public int targetTime;
        public Foggy current;
        public Foggy target;

        public FoggyData(int timer, int targetTime, Foggy current, Foggy target) {
            this.timer = timer;
            this.targetTime = targetTime;
            this.current = current;
            this.target = target;
        }

        public void save(CompoundTag compoundTag) {
            compoundTag.putInt("timer", this.timer);
            compoundTag.putInt("targetTime", this.targetTime);
            compoundTag.putInt("current", this.current.ordinal());
            compoundTag.putInt("target", this.target.ordinal());
        }

        public void load(CompoundTag compoundTag) {
            if (compoundTag == null)
                return;
            this.timer = compoundTag.getInt("timer");
            this.targetTime = compoundTag.getInt("targetTime");
            this.current = Foggy.values()[compoundTag.getInt("current")];
            this.target = Foggy.values()[compoundTag.getInt("target")];
        }
    }

    public static final class ThunderIntensityData {
        public int timer;
        public int targetIntensity;
        public int intensity;

        public ThunderIntensityData(int timer, int targetIntensity, int intensity) {
            this.timer = timer;
            this.targetIntensity = targetIntensity;
            this.intensity = intensity;
        }

        public void save(CompoundTag compoundTag) {
            compoundTag.putInt("timer", this.timer);
            compoundTag.putInt("targetIntensity", this.targetIntensity);
            compoundTag.putInt("intensity", this.intensity);
        }

        public void load(CompoundTag compoundTag) {
            if (compoundTag == null)
                return;
            this.timer = compoundTag.getInt("timer");
            this.targetIntensity = compoundTag.getInt("targetIntensity");
            this.intensity = compoundTag.getInt("intensity");
        }
    }
}
