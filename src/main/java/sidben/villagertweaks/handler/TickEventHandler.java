package sidben.villagertweaks.handler;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import sidben.villagertweaks.tracker.ServerInfoTracker;


public class TickEventHandler
{

    private static final int tickFactor = 300;  // Frequency of update


    @SubscribeEvent
    public void onPostServerTick(TickEvent.ServerTickEvent event)
    {

        if (event.phase == Phase.END) {

            final MinecraftServer server = MinecraftServer.getServer();
            if (server.getTickCounter() % tickFactor == 0) {

                // Cleanup SpecialEventsTracker expired content
                ServerInfoTracker.cleanExpired();

            }
        }

    }



}
