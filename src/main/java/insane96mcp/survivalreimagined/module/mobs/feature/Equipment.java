package insane96mcp.survivalreimagined.module.mobs.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.lootmodifier.DropMultiplierModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.mobs.data.EquipmentDropChance;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Death;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
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
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:zombie", EquipmentSlot.OFFHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:zombified_piglin", EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:piglin", EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:piglin_brute", EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:drowned", EquipmentSlot.OFFHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:zombie_villager", EquipmentSlot.OFFHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:vex", EquipmentSlot.MAINHAND),
            new EquipmentDropChance(IdTagMatcher.Type.ID, "minecraft:pillager", EquipmentSlot.MAINHAND)
    ));
    public static final ArrayList<EquipmentDropChance> equipmentDropChances = new ArrayList<>();
    @Config
    @Label(name = "Drop chance", description = "Set the drop chance for mobs equipment.")
    public static Double dropChance = 1d;

    public Equipment(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        JSON_CONFIGS.add(new JsonConfig<>("custom_drop_chances.json", equipmentDropChances, EQUIPMENT_DROP_CHANCES_DEFAULT, EquipmentDropChance.LIST_TYPE));
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
                || !(event.getEntity() instanceof Mob entity)
                || entity.getPersistentData().contains(Death.PLAYER_GHOST))
            return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            boolean customDropChance = false;
            for (EquipmentDropChance edc : equipmentDropChances) {
                if (edc.matchesEntity(entity) && edc.slot.equals(slot)) {
                    edc.apply(entity);
                    customDropChance = true;
                }
            }
            if (!customDropChance)
                entity.setDropChance(slot, dropChance.floatValue());
        }
    }

    private static final String path = "equipment/";

    public static void addGlobalLoot(GlobalLootModifierProvider provider) {
        provider.add(path + "remove_gold_nuggets_from_piglins", new DropMultiplierModifier.Builder(EntityType.ZOMBIFIED_PIGLIN, Items.GOLD_NUGGET, 0f).build());
    }
}
