package sidben.villagertweaks.tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import sidben.villagertweaks.helper.LogHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/*
 * ----------------------------------------------------------------------------------------
 *   Causal sequence
 * ----------------------------------------------------------------------------------------
 * 
 * IRON GOLEM / SNOW GOLEM
 * 
 * 1) Player places down a pumpkin;
 * 2) Pumpkin block [onBlockAdded] event checks for a pattern and request the spawn of a golem, if a pattern is found;
 * 3) World spawns the golem, firing the [EntityJoinWorldEvent] event;
 * 4) By replacing the pattern blocks with air, world fires the [MultiPlaceEvent] (before the item is removed from player inventory)
 * 
 * In this case I need to intercept when a golem spawn and add to my tracker list. If a compatible MultiPlaceEvent 
 * is fired around that region, I check the pumpkin on the player inventory to add special effects to the golem.
 * 
 */
public class SpecialEventsTracker
{

    public static enum EventType { 
        GOLEM,          // A golem spawned on the world, most likely created by a player 
        ZOMBIE,         // Zombie was about to be cured, used to transfer info to the new villager
        VILLAGER        // Villager was killed by zombie, used to transfer info in case he is zombified
        }

    
    private static final int expireLimit = 100;
    private static boolean isEmpty = true;
    
    private static List<EventTracker> golemTracker = new ArrayList<EventTracker>();
    private static List<EventTracker> zombieTracker = new ArrayList<EventTracker>();
    private static List<EventTracker> villagerTracker = new ArrayList<EventTracker>();
    
    public static boolean canStartTracking = false;
    
    
    
    //@SideOnly(Side.SERVER)
    public static void add(EventType type, int entityID, BlockPos pos) {
        add(type, entityID, pos, "", null);
    }

    
    public static void add(EventType type, int entityID, BlockPos pos, String customName, Object extraInfo) {
        MinecraftServer server = MinecraftServer.getServer();
        add(type, new EventTracker(entityID, pos, customName, extraInfo, server.getTickCounter() + expireLimit));
    }


    /*
    public static void add(EventType type, BlockPos pos, String customName, Object extraInfo) {
        MinecraftServer server = MinecraftServer.getServer();
        add(type, new EventTracker(pos, customName, extraInfo, server.getTickCounter() + expireLimit));
    }
    */
    
    
    //@SideOnly(Side.SERVER)
    public static void add(EventType type, EventTracker event) {
        if (event == null) return;
        if (!canStartTracking) return;
        
        
        LogHelper.info("> Tracking a new event of type [" +type+ "] - [" +event+ "]");
        
        
        switch (type) {
            /*
            case PUMPKIN:
                SpecialEventsTracker.pumpkinTracker.add(event);
                break;
            */
            case GOLEM:
                SpecialEventsTracker.golemTracker.add(event);
                break;

            case ZOMBIE:
                SpecialEventsTracker.zombieTracker.add(event);
                break;
            
            case VILLAGER: 
                SpecialEventsTracker.villagerTracker.add(event);
                break;
            
            default:
                break;
        }

    
        isEmpty = false;
    }
    
    

    //@SideOnly(Side.SERVER)
    /*
     * Seek for an event being tracked around a specific position.
     */
    public static EventTracker seek(EventType type, Vec3i position) {
        LogHelper.info("> Seeking one event of type [" +type+ "] at [" +position+ "]");
        
        final int rangeLimit = 5;      // how far the code will search for an entity. Crafted golems will always return 4, so 5 to play safe.  
        
        switch(type) {
            case GOLEM:
                for(Iterator<EventTracker> i = SpecialEventsTracker.golemTracker.iterator(); i.hasNext(); ) {
                    EventTracker et = i.next();
                    if (position.distanceSq(et.getPosition()) <= rangeLimit) {
                        et.expireNow();
                        LogHelper.info("> found a target [" +et+ "]");
                        return et;
                    }
                }
                break;
                
            default:
                return null;
                    
        }
        
        return null;
    }
    
    
    
    public static void cleanExpired() {
        /*
        if (isEmpty) {
            LogHelper.info("> Tracker empty, nothing to clean");
        }
        else {
            LogHelper.info("> Removing expired events from tracker");
            isEmpty = true;
        }
        */
        
        
        isEmpty = (golemTracker.size() == 0 && villagerTracker.size() == 0 && zombieTracker.size() == 0);
        
        SpecialEventsTracker.debugMe();
        
    }
    
    
    
    private static void debugMe() {
        LogHelper.info("----------------------------------------------------------");
        LogHelper.info("isEmpty = " + SpecialEventsTracker.isEmpty);

        LogHelper.info("Golem Tracker: " + SpecialEventsTracker.golemTracker.size());
        if (SpecialEventsTracker.golemTracker.size() > 0) {
            for(Iterator<EventTracker> i = SpecialEventsTracker.golemTracker.iterator(); i.hasNext(); ) {
                EventTracker e = i.next();
                LogHelper.info("    [" + e.toString() + "]");                
            }
        }
        
        LogHelper.info("Villager Tracker: " + SpecialEventsTracker.villagerTracker.size());
        if (SpecialEventsTracker.villagerTracker.size() > 0) {
            for(Iterator<EventTracker> i = SpecialEventsTracker.villagerTracker.iterator(); i.hasNext(); ) {
                EventTracker e = i.next();
                LogHelper.info("    [" + e.toString() + "]");                
            }
        }

        LogHelper.info("Zombie Tracker: " + SpecialEventsTracker.zombieTracker.size());
        if (SpecialEventsTracker.zombieTracker.size() > 0) {
            for(Iterator<EventTracker> i = SpecialEventsTracker.zombieTracker.iterator(); i.hasNext(); ) {
                EventTracker e = i.next();
                LogHelper.info("    [" + e.toString() + "]");                
            }
        }
        
        LogHelper.info("----------------------------------------------------------");
    }
    
}
