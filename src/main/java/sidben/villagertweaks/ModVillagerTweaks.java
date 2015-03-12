package sidben.villagertweaks;


import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import sidben.villagertweaks.handler.ConfigurationHandler;
import sidben.villagertweaks.proxy.IProxy;
import sidben.villagertweaks.reference.Reference;


@Mod(modid = Reference.ModID, name = Reference.ModName, version = Reference.ModVersion, guiFactory = Reference.GuiFactoryClass)
public class ModVillagerTweaks
{

    // The instance of your mod that Forge uses.
    @Mod.Instance(Reference.ModID)
    public static ModVillagerTweaks instance;


    @SidedProxy(clientSide = Reference.ClientProxyClass, serverSide = Reference.ServerProxyClass)
    public static IProxy            proxy;
    
    // Used to send information between client / server
    public static SimpleNetworkWrapper NetworkWrapper;
    

    


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // Loads config
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        
        // Sided pre-initialization
        proxy.pre_initialize();
    }


    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        // Sided initializations
        proxy.initialize();
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        // Sided post-initialization
        proxy.post_initialize();
    }



}
