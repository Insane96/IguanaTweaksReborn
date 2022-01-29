package insane96mcp.iguanatweaksreborn.module.experience;

import insane96mcp.iguanatweaksreborn.module.experience.feature.*;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Experience")
public class Experience extends Module {

	public GlobalExperience globalExperience;
	public BlockExperience blockExperience;
	public SpawnerMobsExperience spawnerMobsExperience;
	public PlayerExperience playerExperience;
	public OtherExperience otherExperience;

	public Experience() {
		super(Config.builder);
		pushConfig(Config.builder);
		globalExperience = new GlobalExperience(this);
		blockExperience = new BlockExperience(this);
		spawnerMobsExperience = new SpawnerMobsExperience(this);
		playerExperience = new PlayerExperience(this);
		otherExperience = new OtherExperience(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		globalExperience.loadConfig();
		blockExperience.loadConfig();
		spawnerMobsExperience.loadConfig();
		playerExperience.loadConfig();
		otherExperience.loadConfig();
	}
}