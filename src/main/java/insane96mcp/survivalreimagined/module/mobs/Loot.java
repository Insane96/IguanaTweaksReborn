package insane96mcp.survivalreimagined.module.mobs;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

@Label(name = "Loot", description = "Changes mobs loot.")
@LoadFeature(module = Modules.Ids.WORLD)
public class Loot extends Feature {

	public Loot(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "mob_loot_changes", Component.literal("Survival Reimagined Mob Loot Changes"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks));
	}
}