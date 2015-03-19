package sidben.villagertweaks.network;

import scala.actors.threadpool.Arrays;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;


/**
 * Used to notify the client about a golem enchantments.
 *
 */
public class MessageGolemEnchantments implements IMessage
{

    public MessageGolemEnchantments() {}

    public MessageGolemEnchantments(int entityID, int[] enchantmentIds) {
        this._entityID = entityID;
        this._enchantIds = enchantmentIds;
    }
    
    
    
    private int _entityID;
    private int[] _enchantIds;

    
    public int getEntityID() {
        return this._entityID;
    }

    public int[] getEnchantments() {
        return this._enchantIds;
    }
    
    
    
    
    // Reads the packet
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this._entityID = buf.readInt();
        
        int size = buf.readInt();
        if (size <= 0) {
            this._enchantIds = new int[0];
        }
        else
        {
            this._enchantIds = new int[size];
            for (int i = 0; i < size; i++) {
                this._enchantIds[i] = buf.readInt();    
            }
        }
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this._entityID);
        
        if (this.getEnchantments() == null || this.getEnchantments().length <= 0) 
        {
            buf.writeInt(0);        // This byte represents the amount of enchants to read    
        }
        else 
        {
            buf.writeInt(this.getEnchantments().length);        // This byte represents the amount of enchants to read
            for (int i : this.getEnchantments()) {
                buf.writeInt(i);
            }
        }
        
    }


    
    @Override 
    public String toString() {
        StringBuilder r = new StringBuilder();
        
        r.append("Entity ID = ");
        r.append(this.getEntityID());
        r.append(", Enchantments = ");
        r.append(Arrays.toString(this._enchantIds));
        
        return r.toString();
    }

}
