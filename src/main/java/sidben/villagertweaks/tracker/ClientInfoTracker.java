package sidben.villagertweaks.tracker;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.network.ZombieVillagerProfessionMessage;


/**
 * Helper class to manage client-side information.
 *
 */
public class ClientInfoTracker
{

    
    /*
     * This map holds packets sent from Server to Client indicating which zombies
     * has the extended properties, so the client can load the correct skin.
     * 
     * The info comes from [ZombieVillagerProfessionMessage.onMessage] and must be loaded
     * when the client executes [EntityJoinWorldEvent].
     * 
     * I can't use the info before that because when the client receives the packet,
     * the local world still haven't loaded all entities.
     *  
     */
    @SideOnly(Side.CLIENT)
    private static HashMap<Integer, ZombieVillagerProfessionMessage> LoadedZombies = new HashMap<Integer, ZombieVillagerProfessionMessage>();
    
    
    
    @SideOnly(Side.CLIENT)
    public static void addZombieMessage(ZombieVillagerProfessionMessage message) {
        ClientInfoTracker.LoadedZombies.put(message.getEntityID(), message);
    }

    @SideOnly(Side.CLIENT)
    public static ZombieVillagerProfessionMessage getZombieMessage(int entityID) {
        ZombieVillagerProfessionMessage msg = ClientInfoTracker.LoadedZombies.get(entityID);
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
        ZombieVillagerProfessionMessage msg = ClientInfoTracker.getZombieMessage(zombie.getEntityId());
        
        // If a message was found, update the local zombie with that info
        if (msg != null) {
            ExtendedVillagerZombie properties = ExtendedVillagerZombie.get(zombie);
            properties.setProfession(msg.getProfession());
        } 
    }

    
}
