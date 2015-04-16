package sidben.villagertweaks.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import sidben.villagertweaks.helper.GolemEnchantment;
import sidben.villagertweaks.helper.MagicHelper;


/**
 * Adds extra NBT info to iron golems so I can track
 * their buffs when they are enchanted.
 * 
 */
public class ExtendedGolem implements IExtendedEntityProperties
{

    public final static String Identifier   = "GolemInfo";
    protected World myWorld;
    


    // ---------------------------------------------------------
    // Properties
    // ---------------------------------------------------------
    private GolemEnchantment[] enchantments = null;


    public int getEnchantmentsAmount()
    {
        return this.enchantments == null ? 0 : this.enchantments.length;
    }

    public GolemEnchantment[] getEnchantments()
    {
        return this.enchantments;
    }

    public GolemEnchantment getRandomEnchantment()
    {
        int auxAmount = getEnchantmentsAmount();
        
        if (auxAmount == 1) 
        {
            // Just 1 enchantment, returns the first
            return this.getEnchantments()[0];
        }
        if (auxAmount > 1) 
        {
            // More than 1 enchantment, returns a random one
            int raffle = this.myWorld.rand.nextInt(auxAmount);
            return this.getEnchantments()[raffle];
        }
        else 
        {
            // No enchantments, returns nothing
            return null;
        }
    }
    
    public void setEnchantments(int[] enchantmentIds)
    {
        this.enchantments = GolemEnchantment.convertToEnchantArray(enchantmentIds);
    }



    // ---------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------
    public ExtendedGolem() {
    }



    // ---------------------------------------------------------
    // Methods
    // ---------------------------------------------------------

    public static final void register(EntityGolem golem)
    {
        golem.registerExtendedProperties(MagicHelper.GolemEnchantmentsNBTKey, new ExtendedGolem());
    }

    public static final ExtendedGolem get(EntityGolem golem)
    {
        return (ExtendedGolem) golem.getExtendedProperties(MagicHelper.GolemEnchantmentsNBTKey);
    }




    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        final NBTTagCompound properties = (NBTTagCompound) compound.getTag(Identifier);

        if (properties == null) {
            this.enchantments = null;
        } else {
            final int[] ids = properties.getIntArray(MagicHelper.GolemEnchantmentsNBTKey);
            this.setEnchantments(ids);
        }
    }


    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        final NBTTagCompound properties = new NBTTagCompound();

        if (this.enchantments != null && this.enchantments.length > 0) {
            final int[] ids = GolemEnchantment.convert(this.enchantments);
            properties.setIntArray(MagicHelper.GolemEnchantmentsNBTKey, ids);
        }

        compound.setTag(Identifier, properties);
    }


    @Override
    public void init(Entity entity, World world)
    {
        myWorld = world;
    }


}
