package sidben.villagertweaks.network;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sidben.villagertweaks.ModVillagerTweaks;
import sidben.villagertweaks.common.ExtendedGolem;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.helper.GolemEnchantment;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.tracker.ClientInfoTracker;



/*
 * NOTE:
 * 
 * One problem I have with this class is that sending messages to client when the player reaches the "PlayerEvent.StartTracking"
 * event causes the client to receive the pack BEFORE the client loads any entities, so I have to store the message locally and
 * check again on the "EntityJoinWorld" event for the client-side.
 * 
 * One way I could prevent this is, instead of sending the messages instantly, I can store them in a list on the server and send
 * all at once  at a later event, when I expect the client to have all entities loaded.
 * 
 * Would that cause too much network traffic at once? How risky is the desync? Isn't y current method better, since 
 * it uses the client memory instead of server's memory? Need future tests.
 * 
 */


/**
 * Responsible for sending and receiving packets / messages between client and server. 
 *
 */
public class NetworkHelper
{

    
    //---------------------------------------------------------------------
    //      Message Dispatch
    //---------------------------------------------------------------------
    
    /**
     * Send custom info from zombies villagers when the player starts to track them.
     * Mainly used to notify the client of the zombie villager profession, so it
     * can render the correct skin.
     *  
     * Server -> Client 
     */
    public static void sendVillagerProfessionMessage(int zombieId, ExtendedVillagerZombie properties, EntityPlayer target) {
        if (zombieId > 0 && properties != null && properties.getProfession() >= 0) 
        {
            MessageZombieVillagerProfession message = new MessageZombieVillagerProfession(zombieId, properties.getProfession());
            
            // Debug
            LogHelper.info("~~Sending Message - Zombie Profession~~");
            LogHelper.info("    target: " + target);
            LogHelper.info("    " + message.toString());
            
            // Sends a message to the player, with the zombie extra info
            ModVillagerTweaks.NetworkWrapper.sendTo(message, (EntityPlayerMP)target);
        }
    }
    

    /**
     * Send custom info about golems (iron and snow) when the player starts to track them.
     * Mainly used to notify the client of the golem special enchantments, so it
     * can render the correct particles / overlay.
     * 
     * Server -> Client
     * 
     */
    public static void sendEnchantedGolemInfoMessage(int golemId, ExtendedGolem properties, EntityPlayer target) {
        if (golemId > 0 && properties != null) {
            int[] ids = GolemEnchantment.convert(properties.getEnchantments());
            if (ids.length > 0) {
                MessageGolemEnchantments message = new MessageGolemEnchantments(golemId, ids);
                
                // Debug
                LogHelper.info("~~Sending Message - Enchanted Iron Golem info~~");
                LogHelper.info("    target: " + target);
                LogHelper.info("    " + message.toString());
    
                // Sends a message to the player, with the golem extra info
                ModVillagerTweaks.NetworkWrapper.sendTo(message, (EntityPlayerMP)target);
            }
        }
    }
    


    /**
     * Send info about the golems (iron and snow) to all players around a certain radius of them.
     * 
     * Server -> Client
     * 
     */
    public static void sendEnchantedGolemInfoMessage(EntityLiving golem, ExtendedGolem properties) {
        if (golem != null && properties != null) {
            int[] ids = GolemEnchantment.convert(properties.getEnchantments());
            
            if (ids.length > 0) {
                MessageGolemEnchantments message = new MessageGolemEnchantments(golem.getEntityId(), ids);
                TargetPoint target = new TargetPoint(golem.dimension, golem.posX, golem.posY, golem.posZ, 64.0D);
                
                // Debug
                LogHelper.info("~~Sending Message - Enchanted Iron Golem info~~");
                LogHelper.info("    target: " + target);
                LogHelper.info("    " + message.toString());
    
                // Sends a message to the player, with the golem extra info
                ModVillagerTweaks.NetworkWrapper.sendToAllAround(message, target);
            }
        }
    }

    
    
    
    
    
    //---------------------------------------------------------------------
    //      Message Receival
    //---------------------------------------------------------------------

    public static class VillagerProfessionHandler implements IMessageHandler<MessageZombieVillagerProfession, IMessage> {

        @Override
        public IMessage onMessage(MessageZombieVillagerProfession message, MessageContext ctx) {
            
            // Debug
            LogHelper.info("~~Receiving Message - Zombie Profession~~");
            LogHelper.info("    " + message.toString());

            if (message.getEntityID() > 0 && message.getProfession() >= 0) {
                // Saves the info to be used later, when the entity actually loads
                ClientInfoTracker.addZombieMessage(message);
                
                // Attempts to sync the entity. Most of the times the entity won't be found 
                // when this code execute, but on some cases the entity can join the world
                // before the packet is received (e.g. villager gets zombified).
                ClientInfoTracker.SyncZombieMessage(message.getEntityID());
            }
            
            return null;
        }
        
    }

    
    
    public static class GolemEnchantmentHandler implements IMessageHandler<MessageGolemEnchantments, IMessage> {

        @Override
        public IMessage onMessage(MessageGolemEnchantments message, MessageContext ctx)
        {
            // Debug
            LogHelper.info("~~Receiving Message - Enchanted Iron Golem info~~");
            LogHelper.info("    " + message.toString());

            if (message.getEntityID() > 0) {
                // Saves the info to be used later, when the entity actually loads
                ClientInfoTracker.addGolemMessage(message);
                
                // Attempts to sync the entity. Most of the times the entity won't be found 
                // when this code execute, but on some cases the entity can join the world
                // before the packet is received (e.g. villager gets zombified).
                ClientInfoTracker.SyncGolemMessage(message.getEntityID());
            }
            
            return null;
        }
        
    }

    
    
}
