package sidben.villagertweaks.common;

import sidben.villagertweaks.helper.LogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;


//
// refs: 
// jabelarminecraft.blogspot.com.br/p/minecraft-17x.html
// github.com/coolAlias/Forge_Tutorials/blob/master/IExtendedEntityPropertiesTutorial.java
//
public class ExtendedVillagerZombie implements IExtendedEntityProperties
{

    public final static String id = "ExtendedVillagerZombie";
    public final static String keyName = "VillagerInfo";
    protected EntityZombie theEntity;
    protected World theWorld;
    
    private int profession;
    private Boolean flag;
    
    
    
    public int getProfession()
    {
        return this.profession;
    }
    
    
    
    
    
    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        LogHelper.info("saveNBTData");
        LogHelper.info("  - Profession " + this.profession);
        LogHelper.info("  - Flag " + this.flag);
        //LogHelper.info("  - villager " + theEntity.isVillager());
        //LogHelper.info("  - Name " + theEntity.getDisplayName());
        
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Profession", this.profession);
        tag.setBoolean("Defined", this.flag);

        compound.setTag(keyName, tag); 
    }

    
    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        LogHelper.info("loadNBTData");
        //LogHelper.info("  - villager " + theEntity.isVillager());
        //LogHelper.info("  - Name " + theEntity.getDisplayName());
        
        NBTTagCompound tag = (NBTTagCompound)compound.getTag(keyName);

        this.profession = tag.getInteger("Profession");
        this.flag = tag.getBoolean("Defined");
        
        if (!flag) {
            LogHelper.info("   randomizing profession");
//                            this.profession = this.theWorld.rand.nextInt(7) - 2;
//                              if (this.profession < -1) this.profession = -1;

            this.profession = this.theWorld.rand.nextInt(5);
            this.flag = true;
            
            
            tag.setInteger("Profession", this.profession);
            tag.setBoolean("Defined", this.flag);
        }
   
    
        LogHelper.info("  - Profession " + this.profession);
        LogHelper.info("  - Flag " + this.flag);
    }

    
    @Override
    public void init(Entity entity, World world)
    {
        theEntity = (EntityZombie)entity;
        theWorld = world;

        /*
        LogHelper.info("init");
        LogHelper.info("  - villager " + theEntity.isVillager());
        LogHelper.info("  - Name " + theEntity.getCustomNameTag());
        LogHelper.info("  - Profession " + this.profession);
        LogHelper.info("  - Flag " + this.flag);
        */

        
        /*
        this.profession = world.rand.nextInt(7) - 2;
        if (this.profession < -1) this.profession = -1;
        */
    }

}
