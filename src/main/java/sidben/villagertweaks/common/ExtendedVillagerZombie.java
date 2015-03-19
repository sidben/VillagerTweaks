package sidben.villagertweaks.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;


/**
 * Adds extra NBT info to zombie villagers, so I can track info 
 * like the villager original profession. 
 */
public class ExtendedVillagerZombie implements IExtendedEntityProperties
{

    public final static String Identifier = "VillagerInfo";
    protected final static String ProfessionKey = "Profession";             // Controls zombie villager profession
    protected final static String InitializedKey = "Defined";               // Controls if a zombie villager was assigned a profession
    
    @SuppressWarnings("unused")
    private final EntityZombie myLittleZombie;
    protected World myWorld;

    
    
    //---------------------------------------------------------
    // Properties
    //---------------------------------------------------------
    private int profession;
    private Boolean hasValidaData;      // TODO: Attempt to refactor and get rid of this property
    
    
    public int getProfession()
    {
        return this.profession;
    }
    
    public void setProfession(int profession)
    {
        this.profession = profession >= 0 ? profession : -1;
        this.hasValidaData = true;
    }
    
    public void assignRandomProfessionIfNeeded()
    {
        if (this.profession <= -1 && (this.hasValidaData == null || !this.hasValidaData)) {
            //     0 - 4 = gets a real vanilla profession (~70% chance)
            //     < 0   = has no profession, should be a regular vanilla zombie villager
            int p = this.myWorld.rand.nextInt(7) - 2;
            this.setProfession(p);
        }
    }
    
    
    
    
    
    //---------------------------------------------------------
    // Constructor
    //---------------------------------------------------------
    public ExtendedVillagerZombie(EntityZombie zombie)
    {
        this.myLittleZombie = zombie;
        this.profession = -1;
        this.hasValidaData = false;
    }

    
    
    
    //---------------------------------------------------------
    // Methods
    //---------------------------------------------------------
   
    public static final void register(EntityZombie zombie)
    {
        zombie.registerExtendedProperties(ExtendedVillagerZombie.Identifier, new ExtendedVillagerZombie(zombie));
    }
    
    public static final ExtendedVillagerZombie get(EntityZombie zombie)
    {
        return (ExtendedVillagerZombie)zombie.getExtendedProperties(ExtendedVillagerZombie.Identifier);
    }
    
    
    
    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        if (this.hasValidaData == null) {
            this.profession = -1;
            this.hasValidaData = false;
        }
        
        NBTTagCompound properties = new NBTTagCompound();
        properties.setInteger(ProfessionKey, this.profession);
        properties.setBoolean(InitializedKey, this.hasValidaData);

        compound.setTag(Identifier, properties); 
    }

    
    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        NBTTagCompound properties = (NBTTagCompound)compound.getTag(Identifier);

        if (properties == null) {
            hasValidaData = false;
            profession = -1;
        } 
        else {
            this.profession = properties.getInteger(ProfessionKey);
            this.hasValidaData = properties.getBoolean(InitializedKey);
        }

    }

    
    @Override
    public void init(Entity entity, World world)
    {
        myWorld = world;
    }

}
