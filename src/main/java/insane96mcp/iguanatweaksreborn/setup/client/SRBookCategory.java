package insane96mcp.iguanatweaksreborn.setup.client;

import net.minecraft.util.StringRepresentable;

public enum SRBookCategory implements StringRepresentable {
    FORGE_MISC("forge_misc"),
    BLAST_FURNACE_MISC("blast_furnace_misc"),
    SOUL_BLAST_FURNACE_MISC("soul_blast_furnace_misc"),
    FLETCHING_MISC("fletching_misc"),
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
