package sidben.villagertweaks.tracker;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.villagertweaks.common.ExtendedGolem;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.network.MessageGolemEnchantments;
import sidben.villagertweaks.network.MessageZombieVillagerProfession;


/**
 * Helper class to manage client-side information.
 *
 */
public class ClientInfoTracker
{

    
    /*
     * This map holds packets sent from Server to Client indicating which mob
     * has the extended properties, so the client can load the correct skin / overlay / particles.
     * 
     * The info comes from [MetworkHelper.Hanlder] methods and must be loaded
     * when the client executes [EntityJoinWorldEvent].
     * 
     * I can't use the info before that because when the client receives the packet,
     * the local world still haven't loaded all entities (at least on the first load of the world).
     *  
     */
    @SideOnly(Side.CLIENT)
    private static HashMap<Integer, MessageZombieVillagerProfession> LoadedZombies = new HashMap<Integer, MessageZombieVillagerProfession>();

    @SideOnly(Side.CLIENT)
    private static HashMap<Integer, MessageGolemEnchantments> LoadedGolems = new HashMap<Integer, MessageGolemEnchantments>();

    
    
    
    

    //------------------------------------------------------------------------------
    //
    // Zombie Villagers
    //
    //------------------------------------------------------------------------------
    
    @SideOnly(Side.CLIENT)
    public static void addZombieMessage(MessageZombieVillagerProfession message) {
        ClientInfoTracker.LoadedZombies.put(message.getEntityID(), message);
    }

    @SideOnly(Side.CLIENT)
    public static MessageZombieVillagerProfession getZombieMessage(int entityID) {
        MessageZombieVillagerProfession msg = ClientInfoTracker.LoadedZombies.get(entityID);
        ClientInfoTracker.LoadedZombies.remove(entityID);         // removes from the list, it's not needed anymore
        return msg;
    }
    
    
    /**
     * Attempts to locate the entity by it's ID and apply the 
     * extended properties.
     */
    @SideOnly(Side.CLIENT)
    public static void SyncZombieMessage(int entityID) {

        // Seeks if the entity ID is loaded
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        Entity entity = world.getEntityByID(entityID);

        // If found the entity, attempt to sync with info sent by the server 
        if (entity instanceof EntityZombie) {
            ClientInfoTracker.SyncZombieMessage((EntityZombie)entity);
        }
        
    }

    /**
     * Attempts to locate a message with Extended Properties and apply to the zombie. 
     */
    @SideOnly(Side.CLIENT)
    public static void SyncZombieMessage(EntityZombie zombie) {
        
        // Try to locate messages sent by the server, containing special zombie info
        MessageZombieVillagerProfession msg = ClientInfoTracker.getZombieMessage(zombie.getEntityId());
        
        // If a message was found, update the local zombie with that info
        if (msg != null) {
            ExtendedVillagerZombie properties = ExtendedVillagerZombie.get(zombie);
            properties.setProfession(msg.getProfession());
        } 
    }


    
    
    //------------------------------------------------------------------------------
    //
    // Iron / Snow Golems
    //
    //------------------------------------------------------------------------------

    @SideOnly(Side.CLIENT)
    public static void addGolemMessage(MessageGolemEnchantments message) {
        ClientInfoTracker.LoadedGolems.put(message.getEntityID(), message);
    }

    @SideOnly(Side.CLIENT)
    public static MessageGolemEnchantments getGolemMessage(int entityID) {
        MessageGolemEnchantments msg = ClientInfoTracker.LoadedGolems.get(entityID);
        ClientInfoTracker.LoadedGolems.remove(entityID);         // removes from the list, it's not needed anymore
        return msg;
    }
    
    
    /**
     * Attempts to locate the entity by it's ID and apply the 
     * extended properties.
     */
    @SideOnly(Side.CLIENT)
    public static void SyncGolemMessage(int entityID) {

        // Seeks if the entity ID is loaded
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        Entity entity = world.getEntityByID(entityID);

        // If found the entity, attempt to sync with info sent by the server 
        if (entity instanceof EntityIronGolem) {
            ClientInfoTracker.SyncGolemMessage((EntityIronGolem)entity);
        }
        else if (entity instanceof EntitySnowman) {
            ClientInfoTracker.SyncGolemMessage((EntitySnowman)entity);
        }
        
    }

    /**
     * Attempts to locate a message with Extended Properties and apply to the golem. 
     */
    @SideOnly(Side.CLIENT)
    public static void SyncGolemMessage(EntityIronGolem golem) {
        
        // Try to locate messages sent by the server, containing special zombie info
        MessageGolemEnchantments msg = ClientInfoTracker.getGolemMessage(golem.getEntityId());
        
        // If a message was found, update the local zombie with that info
        if (msg != null) {
            ExtendedGolem properties = ExtendedGolem.get(golem);
            properties.setEnchantments(msg.getEnchantments());
        } 
        
    }

    /**
     * Attempts to locate a message with Extended Properties and apply to the golem. 
     */
    @SideOnly(Side.CLIENT)
    public static void SyncGolemMessage(EntitySnowman golem) {
        
        // Try to locate messages sent by the server, containing special zombie info
        MessageGolemEnchantments msg = ClientInfoTracker.getGolemMessage(golem.getEntityId());
        
        // If a message was found, update the local zombie with that info
        if (msg != null) {
            ExtendedGolem properties = ExtendedGolem.get(golem);
            properties.setEnchantments(msg.getEnchantments());
        } 

    }
    
    
}
