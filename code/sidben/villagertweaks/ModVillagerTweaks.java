package sidben.villagertweaks;


import java.util.logging.Level;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;


@Mod(modid=Reference.ModID, name=Reference.ModName, version=Reference.ModVersion)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {Reference.Channel}, packetHandler = sidben.villagertweaks.PacketHandler.class)
public class ModVillagerTweaks {

	

	// The instance of your mod that Forge uses.
	@Instance(Reference.ModID)
	public static ModVillagerTweaks instance;
	
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide=Reference.ClientProxyClass, serverSide=Reference.ServerProxyClass)
	public static CommonProxy proxy;

	
	
	
	
	// Debug mode
	public static boolean onDebug;							// Indicates if the MOD is on debug mode. Extra info will be tracked on the log.

	
	
	
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		try 
        {
        	// loading the configuration from its file
        	config.load();
        	
        	/*
        	// Debug
        	ModVillagerTweaks.onDebug 					= config.get(customRecordCategory, "onDebug", false).getBoolean(false);

        	// Load blocks and items IDs
        	ModVillagerTweaks.redstoneJukeboxIdleID 	= config.getBlock("redstoneJukeboxIdleID", 520).getInt(520);
        	ModVillagerTweaks.redstoneJukeboxActiveID 	= config.getBlock("redstoneJukeboxActiveID", 521).getInt(521);
        	ModVillagerTweaks.blankRecordItemID 		= config.getItem("blankRecordItemID", 7200).getInt(7200);
        	ModVillagerTweaks.customRecordItemID 		= config.getItem("customRecordItemID", 7201).getInt(7201);

        	// Merchant config
        	ModVillagerTweaks.customRecordOffersMin	= config.get(customRecordCategory, "customRecordOffersMin", 2).getInt(2);
        	ModVillagerTweaks.customRecordOffersMax	= config.get(customRecordCategory, "customRecordOffersMax", 4).getInt(4);
        	ModVillagerTweaks.customRecordPriceMin		= config.get(customRecordCategory, "customRecordPriceMin", 5).getInt(5);
        	ModVillagerTweaks.customRecordPriceMax		= config.get(customRecordCategory, "customRecordPriceMax", 9).getInt(9);
        	
        	// Extra validation on the merchant config (min and max values)
        	if (ModVillagerTweaks.customRecordOffersMin < 1) ModVillagerTweaks.customRecordOffersMin = 1;
        	if (ModVillagerTweaks.customRecordOffersMax < ModVillagerTweaks.customRecordOffersMin) ModVillagerTweaks.customRecordOffersMax = ModVillagerTweaks.customRecordOffersMin;
        	if (ModVillagerTweaks.customRecordOffersMax > ModVillagerTweaks.maxOffers) ModVillagerTweaks.customRecordOffersMax = ModVillagerTweaks.maxOffers;
        	if (ModVillagerTweaks.customRecordOffersMin < 1) ModVillagerTweaks.customRecordPriceMin = 1;
        	if (ModVillagerTweaks.customRecordPriceMax < ModVillagerTweaks.customRecordPriceMin) ModVillagerTweaks.customRecordPriceMax = ModVillagerTweaks.customRecordPriceMin;
        	*/
        } 
        catch (Exception e) 
        {
        	FMLLog.log(Level.SEVERE, "Error loading the configuration of the Redstone Jukebox Mod. Error message: " + e.getMessage() + " / " + e.toString());
        } 
        finally 
        {
        	// saving the configuration to its file
        	config.save();
        }
        
        
		
	}
	
	
	@Mod.EventHandler
	public void load(FMLInitializationEvent event) {

		// Register my custom player event handler
		PlayerEventHandler playerEventHandler = new PlayerEventHandler();
		MinecraftForge.EVENT_BUS.register(playerEventHandler);
		

		// Register my custom entity event handler
		EntityEventHandler entityEventHandler = new EntityEventHandler();
		MinecraftForge.EVENT_BUS.register(entityEventHandler);
		

		// Register my custom mob spawn event handler
		MobSpawnController mobSpawnController = new MobSpawnController();
		MinecraftForge.EVENT_BUS.register(mobSpawnController);
		
	}
	
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		// register custom commands
	}

	
	
	
	public static void logDebugInfo(String info)
	{
		logDebug(info, Level.INFO);
	}

	public static void logDebug(String info, Level level)
	{
		if (Reference.ForceDebug) {
			System.out.println(info);
		} else {
			if (onDebug || level != Level.INFO) FMLLog.log("SidbenRedstoneJukebox", level, "Debug: " + info, "");
		}
	}
	

}