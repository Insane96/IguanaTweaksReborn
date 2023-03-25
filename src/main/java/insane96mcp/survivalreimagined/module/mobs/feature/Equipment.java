package insane96mcp.survivalreimagined.module.mobs.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.mobs.data.EquipmentDropChance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Equipment", description = "Changes to mobs equipment")
@LoadFeature(module = Modules.Ids.MOBS)
public class Equipment extends SRFeature {

    public static final ArrayList<EquipmentDropChance> EQUIPMENT_DROP_CHANCES_DEFAULT = new ArrayList<>(List.of(
            new EquipmentDropChance(IdTagMatcher.Type.TAG, "minecraft:skeletons", EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:vex", EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:pillager", EquipmentSlot.MAINHAND)
    ));
    public static final ArrayList<EquipmentDropChance> equipmentDropChances = new ArrayList<>();
    @Config
    @Label(name = "Drop chance", description = "Set the drop chance for mobs equipment.")
    public static Double dropChance = 1d;

    public Equipment(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void loadJsonConfigs() {
        if (!this.isEnabled())
            return;
        super.loadJsonConfigs();
        this.loadAndReadFile("custom_drop_chances.json", equipmentDropChances, EQUIPMENT_DROP_CHANCES_DEFAULT, EquipmentDropChance.LIST_TYPE);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMobSpawn(EntityJoinLevelEvent event) {
        if (!this.isEnabled()
                || !(event.getEntity() instanceof Mob entity))
            return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            boolean customDropChance = false;
            for (EquipmentDropChance edc : equipmentDropChances) {
                if (edc.matchesEntity(entity) && edc.apply(entity)) {
                    customDropChance = true;
                }
            }
            if (!customDropChance)
                entity.setDropChance(slot, dropChance.floatValue());
        }
    }
}
