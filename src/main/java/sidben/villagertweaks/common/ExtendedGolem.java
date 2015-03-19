package sidben.villagertweaks.common;

import sidben.villagertweaks.helper.GolemEnchantment;
import sidben.villagertweaks.helper.MagicHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;


/**
 * Adds extra NBT info to iron golems so I can track 
 * their buffs when they are enchanted.
 *
 */
public class ExtendedGolem implements IExtendedEntityProperties
{

    public final static String Identifier = "GolemInfo";

    
    
    
    //---------------------------------------------------------
    // Properties
    //---------------------------------------------------------
    private GolemEnchantment[] enchantments = null;

    
    public GolemEnchantment[] getEnchantments()
    {
        return this.enchantments;
    }

    public void setEnchantments(int[] enchantmentIds)
    {
        this.enchantments = GolemEnchantment.convert(enchantmentIds);
    }
    
    
    
    //---------------------------------------------------------
    // Constructor
    //---------------------------------------------------------
    public ExtendedGolem()
    {
    }

    
    
    
    //---------------------------------------------------------
    // Methods
    //---------------------------------------------------------

    public static final void register(EntityIronGolem golem)
    {
        golem.registerExtendedProperties(MagicHelper.GolemEnchantmentsNBTKey, new ExtendedGolem());
    }
    
    public static final void register(EntitySnowman golem)
    {
        golem.registerExtendedProperties(MagicHelper.GolemEnchantmentsNBTKey, new ExtendedGolem());
    }
    
    public static final ExtendedGolem get(EntityIronGolem golem)
    {
        return (ExtendedGolem)golem.getExtendedProperties(MagicHelper.GolemEnchantmentsNBTKey);
    }

    public static final ExtendedGolem get(EntitySnowman golem)
    {
        return (ExtendedGolem)golem.getExtendedProperties(MagicHelper.GolemEnchantmentsNBTKey);
    }

    
    
    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        NBTTagCompound properties = (NBTTagCompound)compound.getTag(Identifier);

        if (properties == null) 
        {
            this.enchantments = null;
        } 
        else 
        {
            int[] ids = properties.getIntArray(MagicHelper.GolemEnchantmentsNBTKey);
            this.setEnchantments(ids);

            /*
            NBTTagList enchants = properties.getTagList(MagicHelper.GolemEnchantmentsNBTKey, 3);
            if (enchants.tagCount() > 0)
            {
                int[] ids = new int[enchants.tagCount()];
                for (int i = 0; i < enchants.tagCount(); i++)
                {
                    ids[i] = enchants.getIntArray(p_getIntArray_1_)(i);
                }
            }
            */
        }
    }

    
    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        NBTTagCompound properties = new NBTTagCompound();
        
        if (this.enchantments != null && this.enchantments.length > 0) {
            /*
            int[] ids = new int[this.enchantments.length];
            for (int i = 0; i < ids.length; i++) {
                GolemEnchantment e = this.enchantments[i];
                if (e != null) ids[i] = e.getId();
            }
            */
            int[] ids = GolemEnchantment.convert(this.enchantments);
            properties.setIntArray(MagicHelper.GolemEnchantmentsNBTKey, ids);
        }

        
        /*
        NBTTagList enchants = new NBTTagList();

        if (this.enchantments != null && this.enchantments.length > 0) {
            for (GolemEnchantment e : this.enchantments) {
                if (e != null) enchants.appendTag(new NBTTagInt(e.getId()));
            }
            properties.setTag(MagicHelper.GolemEnchantmentsNBTKey, enchants);
        }
        */

        compound.setTag(Identifier, properties); 
    }

    
    @Override
    public void init(Entity entity, World world)
    {
    }


}
