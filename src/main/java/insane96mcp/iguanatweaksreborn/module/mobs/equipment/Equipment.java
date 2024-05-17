package insane96mcp.iguanatweaksreborn.module.mobs.equipment;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Equipment", description = "Changes to mobs equipment")
@LoadFeature(module = Modules.Ids.MOBS)
public class Equipment extends JsonFeature {

    public static final ArrayList<EquipmentDropChance> EQUIPMENT_DROP_CHANCES_DEFAULT = new ArrayList<>(List.of(
            new EquipmentDropChance(IdTagMatcher.newTag("minecraft:skeletons"), EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.newId("minecraft:zombie"), EquipmentSlot.OFFHAND),
            new EquipmentDropChance(IdTagMatcher.newId("minecraft:zombified_piglin"), EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.newId("minecraft:piglin"), EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.newId("minecraft:piglin_brute"), EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.newId("minecraft:drowned"), EquipmentSlot.OFFHAND),
            new EquipmentDropChance(IdTagMatcher.newId("minecraft:zombie_villager"), EquipmentSlot.OFFHAND),
            new EquipmentDropChance(IdTagMatcher.newId("minecraft:vex"), EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.newId("minecraft:pillager"), EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.newId("progressivebosses:wither_minion"), EquipmentSlot.MAINHAND)
    ));
    public static final ArrayList<EquipmentDropChance> equipmentDropChances = new ArrayList<>();
    @Config
    @Label(name = "Drop chance", description = "Set the drop chance for mobs equipment.")
    public static Double dropChance = 0.5d;
    @Config(min = 0, max = 1)
    @Label(name = "Max durability", description = "Max durability of items dropped by mobs. This also fixes https://bugs.mojang.com/browse/MC-136374. Setting to 0 will disable this feature.")
    public static Double maxDurability = 0.6d;

    public Equipment(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        JSON_CONFIGS.add(new JsonConfig<>("custom_drop_chances.json", equipmentDropChances, EQUIPMENT_DROP_CHANCES_DEFAULT, EquipmentDropChance.LIST_TYPE));
    }

    @Override
    public String getModConfigFolder() {
        return IguanaTweaksReborn.CONFIG_FOLDER;
    }

    @Override
    public void loadJsonConfigs() {
        if (!this.isEnabled())
            return;
        super.loadJsonConfigs();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMobSpawn(EntityJoinLevelEvent event) {
        if (!this.isEnabled()
                || !(event.getEntity() instanceof Mob entity))
            return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            boolean customDropChance = false;
            for (EquipmentDropChance edc : equipmentDropChances) {
                if (edc.entity.matchesEntity(entity) && edc.slot.equals(slot)) {
                    edc.apply(entity);
                    customDropChance = true;
                }
            }
            if (!customDropChance)
                entity.setDropChance(slot, dropChance.floatValue());
        }
    }
}
