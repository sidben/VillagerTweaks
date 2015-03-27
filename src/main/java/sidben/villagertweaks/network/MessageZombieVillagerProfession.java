package sidben.villagertweaks.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;



/**
 * Used to notify the client of the zombie villager profession, 
 * so it can render the correct skin.
 *
 */
public class MessageZombieVillagerProfession implements IMessage
{
    
    
    public MessageZombieVillagerProfession() {}

    public MessageZombieVillagerProfession(int entityID, int profession) {
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

    
    
    
    @Override 
    public String toString() {
        StringBuilder r = new StringBuilder();
        
        r.append("Entity ID = ");
        r.append(this.getEntityID());
        r.append(", Profession = ");
        r.append(this.getProfession());
        
        return r.toString();
    }
    

    
}
