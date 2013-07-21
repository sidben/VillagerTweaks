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
	

	
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		// Loads the config
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		ConfigLoader.load(config);
        
		
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
		

		// Register my custom world event handler
		WorldEventHandler worldEventHandler = new WorldEventHandler();
		MinecraftForge.EVENT_BUS.register(worldEventHandler);
		
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
			if (ConfigLoader.onDebug || level != Level.INFO) FMLLog.log(Reference.ModID, level, "Debug: " + info, "");
		}
	}
	

}