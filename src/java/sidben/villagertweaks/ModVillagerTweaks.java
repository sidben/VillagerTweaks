package sidben.villagertweaks;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import sidben.villagertweaks.handler.ConfigurationHandler;
import sidben.villagertweaks.init.MyBlocks;
import sidben.villagertweaks.init.MyItems;
import sidben.villagertweaks.init.MyRecipes;
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



    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // Loads config
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());

        // Loads items
        MyItems.register();

        // Loads blocks
        MyBlocks.register();
    }


    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        // Item renderer
        MyItems.registerRender();

        // Block renderer
        MyBlocks.registerRender();

        // Recipes
        MyRecipes.register();
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }



}
