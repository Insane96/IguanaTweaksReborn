package insane96mcp.survivalreimagined.setup;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class IntegratedDataPack {

    public static final List<IntegratedDataPack> INTEGRATED_DATA_PACKS = new ArrayList<>();

    PackType packType;
    String path;
    MutableComponent description;
    BooleanSupplier enabled;

    public IntegratedDataPack(PackType packType, String path, MutableComponent description, BooleanSupplier enabled) {
        this.packType = packType;
        this.path = path;
        this.description = description;
        this.enabled = enabled;
    }

    public PackType getPackType() {
        return this.packType;
    }

    public String getPath() {
        return this.path;
    }

    public MutableComponent getDescription() {
        return this.description;
    }

    public boolean shouldBeEnabled() {
        return this.enabled.getAsBoolean();
    }
}
