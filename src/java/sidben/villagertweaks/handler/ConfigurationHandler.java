package sidben.villagertweaks.handler;

import java.io.File;
import sidben.villagertweaks.reference.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class ConfigurationHandler {



	public static Configuration config;



	public static void init(File configFile) {

		// Create configuration object from config file
		if (config == null) {
			config = new Configuration(configFile);
			loadConfig();
		}

	}



	private static void loadConfig()
	{
		// Load properties

		// saving the configuration to its file
    	if (config.hasChanged()) {
            config.save();
    	}
	}




	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase(Reference.ModID))
		{
			// Resync configs
			loadConfig();
		}
	}

}
