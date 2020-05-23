package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;

public class ExperienceModule {
	public static void oreXpDrop(BlockEvent.BreakEvent event) {
		if (!ModConfig.Modules.experience)
			return;

		if (ModConfig.Experience.oreMultiplier == 1.0d)
			return;

		int xpToDrop = event.getExpToDrop();
		xpToDrop *= ModConfig.Experience.oreMultiplier; //2.5d
		event.setExpToDrop(xpToDrop);
	}

	public static void globalXpDrop(EntityJoinWorldEvent event) {
		if (!ModConfig.Modules.experience)
			return;

		if (ModConfig.Experience.globalMultiplier == 1.0d)
			return;

		if (!(event.getEntity() instanceof ExperienceOrbEntity))
			return;

		ExperienceOrbEntity xpOrb = (ExperienceOrbEntity) event.getEntity();

		if (ModConfig.Experience.globalMultiplier == 0d)
			xpOrb.remove();
		else
			xpOrb.xpValue *= ModConfig.Experience.globalMultiplier;

		if (xpOrb.xpValue == 0d)
			xpOrb.remove();
	}

	public static void mobsFromSpawnersXpDrop(LivingExperienceDropEvent event) {
		if (!ModConfig.Modules.experience)
			return;
		if (ModConfig.Experience.mobsFromSpawnersMultiplier == 1.0d)
			return;
		LivingEntity living = event.getEntityLiving();
		CompoundNBT tags = living.getPersistentData();
		if (!tags.getBoolean(Strings.NBTTags.SPAWNED_FROM_SPANWER))
			return;
		int xp = event.getDroppedExperience();
		xp *= ModConfig.Experience.mobsFromSpawnersMultiplier;
		event.setDroppedExperience(xp);
	}
}
