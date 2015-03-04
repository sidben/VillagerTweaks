package sidben.villagertweaks.handler;

import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.tracker.SpecialEventsTracker;
import sidben.villagertweaks.tracker.SpecialEventsTracker.EventType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class TickEventHandler
{
    
    private static final int factor = 200;
    
    

    @SubscribeEvent
    public void onPostServerTick(TickEvent.ServerTickEvent event)
    {

        if (event.phase == Phase.END) {
            SpecialEventsTracker.canStartTracking = true;

            MinecraftServer server = MinecraftServer.getServer();
            if (server.getTickCounter() % factor == 0) {
                
                LogHelper.info("tick [" + server.getTickCounter() + "]");
                SpecialEventsTracker.cleanExpired();
    
            }
        }

    }
    
    

}
