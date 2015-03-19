package sidben.villagertweaks.common;

import net.minecraft.entity.Entity;
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



    // ---------------------------------------------------------
    // Properties
    // ---------------------------------------------------------
    private GolemEnchantment[] enchantments = null;


    public GolemEnchantment[] getEnchantments()
    {
        return this.enchantments;
    }

    public void setEnchantments(int[] enchantmentIds)
    {
        this.enchantments = GolemEnchantment.convert(enchantmentIds);
    }



    // ---------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------
    public ExtendedGolem() {
    }



    // ---------------------------------------------------------
    // Methods
    // ---------------------------------------------------------

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
        return (ExtendedGolem) golem.getExtendedProperties(MagicHelper.GolemEnchantmentsNBTKey);
    }

    public static final ExtendedGolem get(EntitySnowman golem)
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
    }


}
