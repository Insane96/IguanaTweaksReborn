package insane96mcp.survivalreimagined.setup.client;

import net.minecraft.util.StringRepresentable;

public enum SRBookCategory implements StringRepresentable {
    FORGE_MISC("forge_misc"),
    UNKNOWN("unknown");

    public static final StringRepresentable.EnumCodec<SRBookCategory> CODEC = StringRepresentable.fromEnum(SRBookCategory::values);
    private final String name;

    SRBookCategory(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
