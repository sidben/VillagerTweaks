package sidben.villagertweaks.tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.server.MinecraftServer;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.helper.LogHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;



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
 * 4) By replacing the pattern blocks with air, world fires the [MultiPlaceEvent] (before the item is removed from player inventory).
 * 
 * In this case I need to intercept when a golem spawn and add to my tracker list. If a compatible MultiPlaceEvent 
 * is fired around that region, I check the pumpkin on the player inventory to add special effects to the golem.
 * 
 * 
 * 
 * VILLAGER -> ZOMBIE VILLAGER
 * 
 * 1) Villager is killed and the source of damage is EntityZombie;
 * 2) I intercept [onLivingDeath] and add some info of the villager to this tracker - add(EntityVillager villager);
 * 3) World spawns a zombie villager (firing the [EntityJoinWorldEvent] event) at the exact position where the 
 * villager died, ignoring adult/child status (bug?);
 * 4) Players in that region starts tracking the new entity;
 * 
 * Here I keep track of all villagers killed by zombies and add them to a list for a certain time.
 * 
 * When a zombie villager spawns, I check if their spawn coordinates matches any villager that just died and, if so,
 * copy the information I stored on the EventTracker (mainly profession and custom name).
 * 
 * After a zombie spawn, the [PlayerEvent.StartTracking] is fired and I use it to notify the clients about the
 * extended properties that were just copied.
 * 
 *  
 *  
 * ZOMBIE VILLAGER -> VILLAGER
 * 
 * 1) Zombie villager starts to be cured (player used potion and golden apple);
 * 2) EntityZombie fires the [startConversion] inner method, setting the conversion counter to something 
 * between 3600~6000 ticks;
 * 3) On every entity update, the counter is decreased by the amount defined in [getConversionTimeBoost];
 * 4) When that counter reaches zero, EntityZombie fires the [convertToVillager] method, creating a new
 * villager on the same location, preserving adult/child status;
 * 5) After creating the new villager, the zombie is removed with [world.removeEntity] and the villager
 * is added with [world.spawnEntityInWorld];
 * 
 * Here I listen to the [onLivingUpdateEvent], looking for zombie villagers about to convert. I try to 
 * predict the next value of the counter, but since it has a rare random factor (iron bars and beds can give 
 * a boost), it's not 100% reliable.
 * 
 * When I detect the zombie is about to convert on the next tick update, I add it to the tracker list.
 * 
 * I also listen to the [onEntityJoinWorld] and every time a villager joins the world, I check if a zombie
 * villager just converted on that spot so I can copy the info. 
 *
 * 
 */


/**
 * Helper class to store certain server-side information until they are needed or expire.
 * 
 * Used to integrate actions in multiple classes or provide the functionality of non-existing Forge Hooks.
 *
 */
public class ServerInfoTracker
{

    public static enum EventType { 
        GOLEM,          // A golem spawned on the world, most likely created by a player 
        ZOMBIE,         // Zombie was about to be cured, used to transfer info to the new villager
        VILLAGER        // Villager was killed by zombie, used to transfer info in case he is zombified
        }

    
    private static List<EventTracker> golemTracker = new ArrayList<EventTracker>();
    private static List<EventTracker> zombieTracker = new ArrayList<EventTracker>();
    private static List<EventTracker> villagerTracker = new ArrayList<EventTracker>();
    
    private static boolean canStartTracking = false;
    
    
    
    
    /**
     * Adds informations that should be tracked on the server. 
     */
    //public static void add(EventType type, int entityID, BlockPos pos) {
    //    add(type, entityID, pos, "", null);
    //}

    
    /**
     * Adds informations that should be tracked on the server. 
     */
    //public static void add(EventType type, int entityID, BlockPos pos, String customName, Object extraInfo) {
    //    MinecraftServer server = MinecraftServer.getServer();
    //    add(type, new EventTracker(entityID, pos, customName, extraInfo, server.getTickCounter()));
    //}


    /**
     * Adds villager information that should be tracked on the server. 
     */
    public static void add(EntityVillager villager)
    {
        ServerInfoTracker.add(EventType.VILLAGER, new EventTracker(villager));
    }
    
    /**
     * Adds zombie information that should be tracked on the server. 
     */
    public static void add(EntityZombie zombie)
    {
        ExtendedVillagerZombie properties = ExtendedVillagerZombie.get(zombie);
        ServerInfoTracker.add(EventType.ZOMBIE, new EventTracker(zombie, properties));
    }

    /**
     * Adds golem information that should be tracked on the server. 
     */
    public static void add(EntityIronGolem golem)
    {
        ServerInfoTracker.add(EventType.GOLEM, new EventTracker(golem));
    }
    
    /**
     * Adds golem information that should be tracked on the server. 
     */
    public static void add(EntitySnowman golem)
    {
        ServerInfoTracker.add(EventType.GOLEM, new EventTracker(golem));
    }
    
    
    
    /**
     * Adds informations that should be tracked on the server. 
     */
    private static void add(EventType type, EventTracker event) {
        if (event == null) return;
        if (!canStartTracking) return;
        
        
        LogHelper.info("> Tracking a new event of type [" +type+ "] - [" +event+ "]");
        
        
        // Adds the "tick of birth" to control when the event will expire
        MinecraftServer server = MinecraftServer.getServer();
        event.setTOB(server.getTickCounter());
        
        
        // Adds the event to the specific list
        switch (type) {
            case GOLEM:
                ServerInfoTracker.golemTracker.add(event);
                break;

            case ZOMBIE:
                ServerInfoTracker.zombieTracker.add(event);
                break;
            
            case VILLAGER: 
                ServerInfoTracker.villagerTracker.add(event);
                break;
            
            default:
                break;
        }

    
        //isEmpty = false;
    }
    
    

    /**
     * Seek for a valid event being tracked around a specific position.
     */
    public static EventTracker seek(EventType type, Vec3i position) {
        LogHelper.info("> Seeking one event of type [" +type+ "] at [" +position+ "]");
        
        int rangeLimit;             // how far the code will search for an entity.
        int ageTolerance;           // how old the entry can be considered valid.
        
        switch(type) {
            case GOLEM:
                rangeLimit = 5;         // Crafted golem will always return 4, so 5 to play safe.
                ageTolerance = 100;     // Arbitrary value, not really needed here
                
                return seekValueOnList(ServerInfoTracker.golemTracker, position, rangeLimit, ageTolerance);
                
            case VILLAGER:
                rangeLimit = 1;         // Seek at the exact spot (maybe should be zero?)
                ageTolerance = 5;       // Only 5 ticks tolerance to play safe, since "zombification" happens on the same tick 
                
                return seekValueOnList(ServerInfoTracker.villagerTracker, position, rangeLimit, ageTolerance);
                
            case ZOMBIE:
                rangeLimit = 1;         // Seek at the exact spot (maybe should be zero?)
                ageTolerance = 5;       // Only 3 ticks tolerance to play safe, since the cure should happen on the next 1 tick 
                
                return seekValueOnList(ServerInfoTracker.zombieTracker, position, rangeLimit, ageTolerance);
                
            default:
                return null;
                    
        }
        
    }
    

    /**
     * Encapsulates the search on a list.
     * 
     * @param list Target list
     * @param position Desired coordinates
     * @param rangeLimit Maximum distance accepted
     * @param ageTolerance Maximum age of event accepted 
     */
    private static EventTracker seekValueOnList(List<EventTracker> list, Vec3i position, int rangeLimit, int ageTolerance) {
        MinecraftServer server = MinecraftServer.getServer();
        
        for(Iterator<EventTracker> i = list.iterator(); i.hasNext(); ) {
            EventTracker et = i.next();
            
            // TEMP DEBUG
            LogHelper.info("seeking [" + et + "]");
            if (!(et.getTOB() > 0)) LogHelper.info("   too expired...");
            if (!(et.getTOB() + ageTolerance >= server.getTickCounter())) LogHelper.info("   too old...");
            if (!(position.distanceSq(et.getPosition()) <= rangeLimit)) LogHelper.info("   too far...");
            
            
            if (et.getTOB() > 0 && et.getTOB() + ageTolerance >= server.getTickCounter() && position.distanceSq(et.getPosition()) <= rangeLimit) {
                LogHelper.info("> found a valid target [" + et + "]");
                et.expireNow();
                return et;
            }
        }
        
        return null;
    }
    
    
    
    /**
     * Remove all expired information from the tracked lists.
     */
    public static void cleanExpired() {
        if (!ServerInfoTracker.canStartTracking) return;
        
        MinecraftServer server = MinecraftServer.getServer();
        int expireAllUntil = server.getTickCounter() - 100;     // Maximmum amount of time an entry stays at the list
        
        LogHelper.info("   -- Cleanup (" + expireAllUntil + ") --   ");

        
        cleanExpiredList(ServerInfoTracker.golemTracker, expireAllUntil);
        cleanExpiredList(ServerInfoTracker.villagerTracker, expireAllUntil);
        cleanExpiredList(ServerInfoTracker.zombieTracker, expireAllUntil);
        
        ServerInfoTracker.debugMe();
    }
    
    /**
     * Encapsulates the cleaning of a list.
     * 
     * @param list Target list
     * @param limit Last accepted tick. Everything before this will be expired.
     */
    private static void cleanExpiredList(List<EventTracker> list, int limit) {
        if (list.size() > 0) {
            Iterator<EventTracker> i = list.iterator();

            while (i.hasNext()) {
                EventTracker et = i.next();
                if (et.getTOB() < 0 || et.getTOB() < limit) {
                    LogHelper.info("      removing: " + et);
                    i.remove();
                }
             }
            
        }
    }
    
    

    /*
     * NOTE: This exists to avoid using the tracker before it's actually needed. 
     * Not essential after the auto-cleaning was finished, may be removed in the future.
     * 
     * e.g. - Tracking player-created iron golem.
     */
    
    /**
     * Allows the tracker to start working.
     */
    public static void startTracking() {
        LogHelper.info("   ServerInfoTracker.startTracking()");
        
        canStartTracking = true;
        
        golemTracker.clear();
        zombieTracker.clear();
        villagerTracker.clear();
    }
    
    
    
    private static void debugMe() {
        LogHelper.info("----------------------------------------------------------");
        //LogHelper.info("isEmpty = " + SpecialEventsTracker.isEmpty);

        LogHelper.info("Golem Tracker: " + ServerInfoTracker.golemTracker.size());
        if (ServerInfoTracker.golemTracker.size() > 0) {
            for(Iterator<EventTracker> i = ServerInfoTracker.golemTracker.iterator(); i.hasNext(); ) {
                EventTracker e = i.next();
                LogHelper.info("    [" + e.toString() + "]");                
            }
        }
        
        LogHelper.info("Villager Tracker: " + ServerInfoTracker.villagerTracker.size());
        if (ServerInfoTracker.villagerTracker.size() > 0) {
            for(Iterator<EventTracker> i = ServerInfoTracker.villagerTracker.iterator(); i.hasNext(); ) {
                EventTracker e = i.next();
                LogHelper.info("    [" + e.toString() + "]");                
            }
        }

        LogHelper.info("Zombie Tracker: " + ServerInfoTracker.zombieTracker.size());
        if (ServerInfoTracker.zombieTracker.size() > 0) {
            for(Iterator<EventTracker> i = ServerInfoTracker.zombieTracker.iterator(); i.hasNext(); ) {
                EventTracker e = i.next();
                LogHelper.info("    [" + e.toString() + "]");                
            }
        }
        
        LogHelper.info("----------------------------------------------------------");
    }



    
}
