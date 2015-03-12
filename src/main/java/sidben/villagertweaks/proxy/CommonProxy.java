package sidben.villagertweaks.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import sidben.villagertweaks.ModVillagerTweaks;
import sidben.villagertweaks.handler.EntityEventHandler;
import sidben.villagertweaks.handler.PlayerEventHandler;
import sidben.villagertweaks.handler.TickEventHandler;
import sidben.villagertweaks.handler.WorldEventHandler;
import sidben.villagertweaks.init.MyAchievements;
import sidben.villagertweaks.init.MyBlocks;
import sidben.villagertweaks.network.ZombieVillagerProfessionMessage;
import sidben.villagertweaks.reference.Reference;

public abstract class CommonProxy implements IProxy {
    
    
    @Override
    public void pre_initialize()
    {
        // Register network messages
        ModVillagerTweaks.NetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.ModChannel);
        ModVillagerTweaks.NetworkWrapper.registerMessage(ZombieVillagerProfessionMessage.Handler.class, ZombieVillagerProfessionMessage.class, 0, Side.CLIENT);
        
        // Register blocks
        MyBlocks.register();
    }

    
    @Override
    public void initialize()
    {
        // Achievements
        MyAchievements.register();

        // Event Handlers
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
        MinecraftForge.EVENT_BUS.register(new WorldEventHandler());

        FMLCommonHandler.instance().bus().register(new TickEventHandler());
    }


    @Override
    public void post_initialize()
    {
    }

    
}
