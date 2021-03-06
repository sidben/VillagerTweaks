package sidben.villagertweaks.handler;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sidben.villagertweaks.reference.Reference;


public class ConfigurationHandler
{

    // Indicates if the mod is on debug mode. Extra info will be tracked on the log.
    public static boolean       onDebug                   = false;

    // Main config values
    public static boolean       canNameVillagers          = true;
    public static boolean       zombieVillagerDropEmerald = true;
    public static boolean       zombieDropFeather         = true;
    public static int       enchantedPumpkinDropChance         = 40;


    // Instance
    public static Configuration config;



    public static void init(File configFile)
    {

        // Create configuration object from config file
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }

    }



    private static void loadConfig()
    {
        // Load properties
        onDebug = config.getBoolean("on_debug", Configuration.CATEGORY_GENERAL, false, "Adds extra info messages to the forge log.");
        canNameVillagers = config.getBoolean("villager_naming", Configuration.CATEGORY_GENERAL, true, "Enables naming villagers with Name Tags.");
        zombieVillagerDropEmerald = config.getBoolean("zombie_villager_drops_emerald", Configuration.CATEGORY_GENERAL, true, "Enables zombie villagers dropping emeralds.");
        zombieDropFeather = config.getBoolean("zombie_drops_feather", Configuration.CATEGORY_GENERAL, true, "Enables regular zombies dropping feathers.");
        enchantedPumpkinDropChance = config.getInt("ench_pumpkin_drop_chance", Configuration.CATEGORY_GENERAL, 40, 0, 100, "Chance of enchanted golems dropping the enchanted pumpkin on death.");
//        uniqueEnchantedPumpkinDropChance = config.getInt("unique_ench_drop_chance", Configuration.CATEGORY_GENERAL, 50, 0, 100, "Chance of 'Migthy' enchanted golems dropping the enchanted pumpkin on death.");

        // saving the configuration to its file
        if (config.hasChanged()) {
            config.save();
        }
    }



    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equalsIgnoreCase(Reference.ModID)) {
            // Resync config
            loadConfig();
        }
    }

}
