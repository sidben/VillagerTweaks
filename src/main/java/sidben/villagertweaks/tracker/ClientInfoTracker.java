package sidben.villagertweaks.tracker;

import java.util.HashMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

    
}
