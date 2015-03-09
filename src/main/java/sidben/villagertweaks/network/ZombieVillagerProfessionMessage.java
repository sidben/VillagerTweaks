package sidben.villagertweaks.network;

import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.tracker.ClientInfoTracker;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;



/**
 * Used to notify the client of the zombie villager profession, 
 * so it can render the correct skin.
 *
 */
public class ZombieVillagerProfessionMessage implements IMessage
{
    
    
    public ZombieVillagerProfessionMessage() {}

    public ZombieVillagerProfessionMessage(int entityID, int profession) {
        this._entityID = entityID;
        this._profession = profession;
    }


    
    
    private int _entityID;
    private int _profession;
    
    public int getProfession() {
        return this._profession;
    }
    
    public int getEntityID() {
        return this._entityID;
    }
    
    

    // Reads the packet
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this._entityID = buf.readInt();
        this._profession = buf.readInt();
        // note - maybe use ByteBufUtils
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this._entityID);
        buf.writeInt(this._profession);
    }

    
    
    
    
    
    
    
    
    public static class Handler implements IMessageHandler<ZombieVillagerProfessionMessage, IMessage> {

        @Override
        public IMessage onMessage(ZombieVillagerProfessionMessage message, MessageContext ctx) {
            
            LogHelper.info("Woo, I got a package!");
            LogHelper.info("    " + ctx.side);
            LogHelper.info("    Entity [" + message._entityID + "], Profession [" + message._profession + "]");
            
            if (message._entityID > 0 && message._profession >= 0) {
                // Saves the info to be used later, when the entity actually loads
                ClientInfoTracker.addZombieMessage(message);
            }
            
            return null;
        }
        
    }
    
}
