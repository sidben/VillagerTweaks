package sidben.villagertweaks;


import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import sidben.villagertweaks.client.renderer.entity.RenderCrackedIronGolem;
import sidben.villagertweaks.client.renderer.entity.RenderZombieVillager;
import sidben.villagertweaks.creativetab.ModCreativeTabs;
import sidben.villagertweaks.dispenser.BehaviorMagicPumpkinDispense;
import sidben.villagertweaks.handler.ConfigurationHandler;
import sidben.villagertweaks.handler.EntityEventHandler;
import sidben.villagertweaks.handler.PlayerEventHandler;
import sidben.villagertweaks.handler.TickEventHandler;
import sidben.villagertweaks.handler.WorldEventHandler;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.init.MyAchievements;
import sidben.villagertweaks.network.MessageGolemEnchantments;
import sidben.villagertweaks.network.NetworkHelper;
import sidben.villagertweaks.network.MessageZombieVillagerProfession;
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
    
    
    public static SimpleNetworkWrapper NetworkWrapper;
    
    
    public static CreativeTabs tabMod = new ModCreativeTabs("tabVillagerTweaks");

    
    

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // Loads config
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        
        // Register network messages
        NetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.ModChannel);
        NetworkWrapper.registerMessage(NetworkHelper.VillagerProfessionHandler.class, MessageZombieVillagerProfession.class, 0, Side.CLIENT);
        NetworkWrapper.registerMessage(NetworkHelper.GolemEnchantmentHandler.class, MessageGolemEnchantments.class, 1, Side.CLIENT);
        
    }


    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        // Achievements
        MyAchievements.register();

        // Event Handlers
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
        MinecraftForge.EVENT_BUS.register(new WorldEventHandler());

        FMLCommonHandler.instance().bus().register(new TickEventHandler());
        
        // Entity renderer
        if (event.getSide() == Side.CLIENT) {
            Minecraft.getMinecraft().getRenderManager().entityRenderMap.remove(EntityIronGolem.class);
            Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityIronGolem.class, new RenderCrackedIronGolem(Minecraft.getMinecraft().getRenderManager()));

            Minecraft.getMinecraft().getRenderManager().entityRenderMap.remove(EntityZombie.class);
            Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityZombie.class, new RenderZombieVillager(Minecraft.getMinecraft().getRenderManager()));
        }
        
        // Custom dispenser behavior
        LogHelper.debug("Overriding pumpkin dispenser behaviour");
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.pumpkin), new BehaviorMagicPumpkinDispense());

    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }



}
