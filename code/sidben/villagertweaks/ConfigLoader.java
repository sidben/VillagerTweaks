package sidben.villagertweaks;

import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.Configuration;


class ConfigLoader {
	
	
	// Debug mode
	public static boolean onDebug;							// Indicates if the MOD is on debug mode. Extra info will be tracked on the log.

	// Main config values
	public static boolean zombieVillagerDropEmerald = true;
	public static boolean canNameVillagers = true;
	public static boolean witchBonusSpawn = true;
	public static int witchChanceBasic = 2;
	public static int witchChanceRain = 4;
	public static boolean zombieDropFeather = true;

	
	// Custom categories
	static final String CategoryGlobal = "global";
	static final String CategoryZombieVillagerEmeralds = "zombie_villager_drops_emerald";
	static final String CategoryVillagerNameTags = "villager_name_tags";
	static final String CategoryWitchSpawn = "witch_moon_spawn";
	static final String CategoryZombieDropsFeather = "zombie_drops_feather";
	

	
	
	static void load(Configuration config)
	{

		try 
        {
        	// loading the configuration from its file
        	config.load();
        	
        	
        	// Debug
        	ConfigLoader.onDebug 					= config.get(ConfigLoader.CategoryGlobal, "onDebug", false).getBoolean(false);

        	// Mod Config
        	ConfigLoader.zombieVillagerDropEmerald	= config.get(ConfigLoader.CategoryZombieVillagerEmeralds, "enabled", true).getBoolean(true);

        	ConfigLoader.canNameVillagers	= config.get(ConfigLoader.CategoryVillagerNameTags, "enabled", true).getBoolean(true);
        	
        	ConfigLoader.witchBonusSpawn	= config.get(ConfigLoader.CategoryWitchSpawn, "enabled", true).getBoolean(true);
        	ConfigLoader.witchChanceBasic	= config.get(ConfigLoader.CategoryWitchSpawn, "weightNormal", 2).getInt(2);
        	ConfigLoader.witchChanceRain	= config.get(ConfigLoader.CategoryWitchSpawn, "weightRaining", 4).getInt(4);
        	
        	ConfigLoader.zombieDropFeather	= config.get(ConfigLoader.CategoryZombieDropsFeather, "enabled", true).getBoolean(true);

        } 
        catch (Exception e) 
        {
        	FMLLog.log(Level.SEVERE, "Error loading the configuration of the Villager Tweaks Mod. Error message: " + e.getMessage() + " / " + e.toString());
        } 
        finally 
        {
        	// saving the configuration to its file
        	config.save();
        }

		ModVillagerTweaks.logDebugInfo("Config loaded:");
		ModVillagerTweaks.logDebugInfo("    Villager zombies drops emerald: " + ConfigLoader.zombieVillagerDropEmerald);
		ModVillagerTweaks.logDebugInfo("    Adult Villager can be named: " + ConfigLoader.canNameVillagers);
		ModVillagerTweaks.logDebugInfo("    Witch Spawn on swamps: " + ConfigLoader.witchBonusSpawn);
		ModVillagerTweaks.logDebugInfo("    Zombies drops feather: " + ConfigLoader.zombieVillagerDropEmerald);

	}
	

}
